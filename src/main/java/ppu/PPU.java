package ppu;

import cpu.interrupt.InterruptRequester;
import cpu.register.enums.INTERRUPT;
import mem.MemoryConstants;
import mem.OAM;
import mem.VRAM;
import shared.Addressable;
import shared.Clocked;
import shared.Component;
import snapshot.PictureRegisterSnapshot;

public class PPU implements Clocked, Addressable, Component {

    private final VRAM vram;
    private final OAM oam;
    private final InterruptRequester interrupts;

    private int lcdc;
    private int stat;
    private int scy;
    private int scx;
    private int ly;
    private int lyc;
    private int bgp;
    private int obp0;
    private int obp1;
    private int wy;
    private int wx;

    private PPUMode mode;
    private int dot;
    private int windowLineCounter;
    private boolean statInterruptLine;

    private final FrameBuffer frameBuffer;
    private final int[] scanlineBuffer;
    private final int[] bgPriorityBuffer;

    private final int[] spriteX = new int[10];
    private final int[] spriteY = new int[10];
    private final int[] spriteTile = new int[10];
    private final int[] spriteFlags = new int[10];
    private int spriteCount;

    public PPU(VRAM vram, OAM oam, InterruptRequester interrupts) {
        this.vram = vram;
        this.oam = oam;
        this.interrupts = interrupts;

        this.frameBuffer = new FrameBuffer();
        this.scanlineBuffer = new int[FrameConstants.WIDTH];
        this.bgPriorityBuffer = new int[FrameConstants.WIDTH];

        reset();
    }

    @Override
    public void tick(int cycles) {
        if (!isLCDEnabled()) { return; }
        for (int i = 0; i < cycles; i++) { tickDot(); }
    }

    private void tickDot() {
        dot++;

        switch (mode) {
            case OAM_SCAN -> {
                if (dot >= FrameConstants.OAM_SCAN_DOTS) {
                    oamScan();
                    setMode(PPUMode.DRAWING);
                }
            }
            case DRAWING -> {
                if (dot >= FrameConstants.OAM_SCAN_DOTS + FrameConstants.DRAWING_MIN_DOTS) {
                    renderScanline();
                    setMode(PPUMode.HBLANK);
                }
            }
            case HBLANK -> {
                if (dot >= FrameConstants.DOTS_PER_SCANLINE) {
                    dot = 0;
                    ly++;

                    if (ly >= FrameConstants.HEIGHT) {
                        setMode(PPUMode.VBLANK);
                        interrupts.request(INTERRUPT.VBLANK);
                        frameBuffer.swapBuffers();
                    } else {
                        setMode(PPUMode.OAM_SCAN);
                    }

                    checkLycInterrupt();
                }
            }
            case VBLANK -> {
                if (dot >= FrameConstants.DOTS_PER_SCANLINE) {
                    dot = 0;
                    ly++;

                    if (ly >= FrameConstants.TOTAL_SCANLINES) {
                        ly = 0;
                        windowLineCounter = 0;
                        setMode(PPUMode.OAM_SCAN);
                    }

                    checkLycInterrupt();
                }
            }
        }
    }

    private void setMode(PPUMode newMode) {
        mode = newMode;
        checkStatInterrupt();
    }

    private void checkStatInterrupt() {
        boolean interrupt = false;

        switch (mode) {
            case HBLANK  -> interrupt = (stat & FrameConstants.STAT_MODE0_INT) != 0;
            case VBLANK  -> interrupt = (stat & FrameConstants.STAT_MODE1_INT) != 0;
            case OAM_SCAN -> interrupt = (stat & FrameConstants.STAT_MODE2_INT) != 0;
        }

        if ((stat & FrameConstants.STAT_LYC_INT) != 0 && ly == lyc) {
            interrupt = true;
        }

        if (interrupt && !statInterruptLine) {
            interrupts.request(INTERRUPT.LCD_STAT);
        }
        statInterruptLine = interrupt;
    }

    private void checkLycInterrupt() {
        checkStatInterrupt();
    }

    private void oamScan() {
        spriteCount = 0;
        int spriteHeight = (lcdc & FrameConstants.LCDC_OBJ_SIZE) != 0 ? 16 : 8;
        byte[] oamData = oam.directMemoryAccess();

        for (int i = 0; i < 40 && spriteCount < 10; i++) {
            int offset = i * 4;
            int y = (oamData[offset] & 0xFF) - 16;
            int x = (oamData[offset + 1] & 0xFF) - 8;
            int tile = oamData[offset + 2] & 0xFF;
            int flags = oamData[offset + 3] & 0xFF;

            if (ly >= y && ly < y + spriteHeight) {
                spriteY[spriteCount] = y;
                spriteX[spriteCount] = x;
                spriteTile[spriteCount] = tile;
                spriteFlags[spriteCount] = flags;
                spriteCount++;
            }
        }
    }

    private void renderScanline() {
        java.util.Arrays.fill(scanlineBuffer, 0);
        java.util.Arrays.fill(bgPriorityBuffer, 0);

        if ((lcdc & FrameConstants.LCDC_BG_ENABLE) != 0) {
            renderBackground();
        }

        if ((lcdc & FrameConstants.LCDC_WIN_ENABLE) != 0 && (lcdc & FrameConstants.LCDC_BG_ENABLE) != 0) {
            renderWindow();
        }

        if ((lcdc & FrameConstants.LCDC_OBJ_ENABLE) != 0) {
            renderSprites();
        }

        for (int x = 0; x < FrameConstants.WIDTH; x++) {
            frameBuffer.setPixel(x, ly, scanlineBuffer[x]);
        }
    }

    private void renderBackground() {
        int tileMapBase = (lcdc &FrameConstants. LCDC_BG_TILEMAP) != 0 ? 0x1C00 : 0x1800;

        boolean signedAddressing = (lcdc & FrameConstants.LCDC_TILE_DATA) == 0;
        int tileDataBase = signedAddressing ? 0x1000 : 0x0000;

        int y = (ly + scy) & 0xFF;
        int tileRow = y / 8;
        int tileY = y % 8;

        for (int x = 0; x < FrameConstants.WIDTH; x++) {
            int scrolledX = (x + scx) & 0xFF;
            int tileCol = scrolledX / 8;
            int tileX = scrolledX % 8;

            int tileMapAddr = tileMapBase + tileRow * 32 + tileCol;
            int tileIndex = vram.directMemoryAccess()[tileMapAddr] & 0xFF;

            if (signedAddressing) {
                tileIndex = (byte) tileIndex;  // Sign extend
            }

            int tileAddr = tileDataBase + tileIndex * 16 + tileY * 2;
            int color = getTilePixel(tileAddr, 7 - tileX);

            scanlineBuffer[x] = applyPalette(color, bgp);
            bgPriorityBuffer[x] = color;
        }
    }

    private void renderWindow() {
        if (ly < wy || wx > 166) {
            return;
        }

        int windowX = wx - 7;
        if (windowX < 0) windowX = 0;

        int tileMapBase = (lcdc & FrameConstants.LCDC_WIN_TILEMAP) != 0 ? 0x1C00 : 0x1800;

        boolean signedAddressing = (lcdc & FrameConstants.LCDC_TILE_DATA) == 0;
        int tileDataBase = signedAddressing ? 0x1000 : 0x0000;

        int y = windowLineCounter;
        int tileRow = y / 8;
        int tileY = y % 8;

        boolean windowUsed = false;

        for (int x = windowX; x < FrameConstants.WIDTH; x++) {
            int winX = x - windowX;
            int tileCol = winX / 8;
            int tileX = winX % 8;

            int tileMapAddr = tileMapBase + tileRow * 32 + tileCol;
            int tileIndex = vram.directMemoryAccess()[tileMapAddr] & 0xFF;

            if (signedAddressing) {
                tileIndex = (byte) tileIndex;
            }

            int tileAddr = tileDataBase + tileIndex * 16 + tileY * 2;

            int color = getTilePixel(tileAddr, 7 - tileX);

            scanlineBuffer[x] = applyPalette(color, bgp);
            bgPriorityBuffer[x] = color;
            windowUsed = true;
        }

        if (windowUsed) {
            windowLineCounter++;
        }
    }

    private void renderSprites() {
        for (int i = spriteCount - 1; i >= 0; i--) {
            int x = spriteX[i];
            int y = spriteY[i];
            int tile = spriteTile[i];
            int flags = spriteFlags[i];

            boolean flipX = (flags & 0x20) != 0;
            boolean flipY = (flags & 0x40) != 0;
            boolean bgPriority = (flags & 0x80) != 0;
            int palette = (flags & 0x10) != 0 ? obp1 : obp0;

            int spriteHeight = (lcdc & FrameConstants.LCDC_OBJ_SIZE) != 0 ? 16 : 8;
            int spriteY = ly - y;

            if (flipY) {
                spriteY = spriteHeight - 1 - spriteY;
            }

            if (spriteHeight == 16) {
                tile &= 0xFE;
            }

            int tileAddr = tile * 16 + spriteY * 2;

            for (int px = 0; px < 8; px++) {
                int screenX = x + px;
                if (screenX < 0 || screenX >= FrameConstants.WIDTH) continue;

                int tileX = flipX ? px : (7 - px);
                int color = getTilePixel(tileAddr, tileX);

                if (color == 0) continue;

                if (bgPriority && bgPriorityBuffer[screenX] != 0) continue;

                scanlineBuffer[screenX] = applyPalette(color, palette);
            }
        }
    }

    private int getTilePixel(int tileAddr, int bit) {
        byte[] data = vram.directMemoryAccess();
        int lo = data[tileAddr] & 0xFF;
        int hi = data[tileAddr + 1] & 0xFF;

        int loBit = (lo >> bit) & 1;
        int hiBit = (hi >> bit) & 1;

        return (hiBit << 1) | loBit;
    }

    private int applyPalette(int color, int palette) {
        return (palette >> (color * 2)) & 0x03;
    }

    @Override
    public boolean accepts(int address) {
        return address >= MemoryConstants.LCDC && address <= MemoryConstants.WX && address != MemoryConstants.DMA;
    }

    @Override
    public byte read(int address) {
        return switch (address) {
            case MemoryConstants.LCDC -> (byte) lcdc;
            case MemoryConstants.STAT -> (byte) (0x80 | (stat & 0x78) | (ly == lyc ? 0x04 : 0) | mode.flag());
            case MemoryConstants.SCY  -> (byte) scy;
            case MemoryConstants.SCX  -> (byte) scx;
            case MemoryConstants.LY   -> (byte) ly;
            case MemoryConstants.LYC  -> (byte) lyc;
            case MemoryConstants.BGP  -> (byte) bgp;
            case MemoryConstants.OBP0 -> (byte) obp0;
            case MemoryConstants.OBP1 -> (byte) obp1;
            case MemoryConstants.WY   -> (byte) wy;
            case MemoryConstants.WX   -> (byte) wx;
            default   -> (byte) 0xFF;
        };
    }

    @Override
    public void write(int address, int value) {
        value &= 0xFF;

        switch (address) {
            case MemoryConstants.LCDC -> {
                boolean wasEnabled = isLCDEnabled();
                lcdc = value;
                if (wasEnabled && !isLCDEnabled()) {
                    ly = 0;
                    dot = 0;
                    mode = PPUMode.HBLANK;
                } else if (!wasEnabled && isLCDEnabled()) {
                    mode = PPUMode.OAM_SCAN;
                    dot = 0;
                }
            }
            case MemoryConstants.STAT -> stat = (stat & 0x07) | (value & 0x78);
            case MemoryConstants.SCY  -> scy = value;
            case MemoryConstants.SCX  -> scx = value;
            case MemoryConstants.LY   -> {}
            case MemoryConstants.LYC  -> { lyc = value; checkLycInterrupt(); }
            case MemoryConstants.BGP  -> bgp = value;
            case MemoryConstants.OBP0 -> obp0 = value;
            case MemoryConstants.OBP1 -> obp1 = value;
            case MemoryConstants.WY   -> wy = value;
            case MemoryConstants.WX   -> wx = value;
        }
    }

    @Override
    public void reset() {
        lcdc = 0x91;
        stat = 0x00;
        scy = 0;
        scx = 0;
        ly = 0;
        lyc = 0;
        bgp = 0xFC;
        obp0 = 0xFF;
        obp1 = 0xFF;
        wy = 0;
        wx = 0;

        mode = PPUMode.OAM_SCAN;
        dot = 0;
        windowLineCounter = 0;
        statInterruptLine = false;

        frameBuffer.clear();
    }

    public boolean isLCDEnabled() {
        return (lcdc & FrameConstants.LCDC_ENABLE) != 0;
    }

    public PPUMode getMode() { return mode; }
    public int getLY() { return ly; }
    public int getDot() { return dot; }

    public FrameBuffer getFrameBuffer() { return frameBuffer; }

    public boolean isVBlankStart() {
        return mode == PPUMode.VBLANK && ly == 144 && dot == 0;
    }

    public PictureRegisterSnapshot createSnapshot() {
        return new PictureRegisterSnapshot(
                lcdc & 0xFF,
                stat & 0xFF,
                scy & 0xFF,
                scx & 0xFF,
                ly & 0xFF,
                lyc & 0xFF,
                bgp & 0xFF,
                obp0 & 0xFF,
                obp1 & 0xFF,
                wy & 0xFF,
                wx & 0xFF,
                mode
        );
    }
}
