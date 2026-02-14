package mem;

import cart.Cartridge;
import cpu.interrupt.InterruptController;
import cpu.interrupt.Timer;
import io.JoyPad;
import io.Serial;
import ppu.PPU;
import ppu.PPUMode;
import shared.Addressable;
import shared.Component;

public class MemoryManagementUnit implements Addressable, Component {

    private Cartridge cart;
    private final VRAM vram;
    private final WRAM wram;
    private final OAM oam;
    private final IO io;
    private final HRAM hram;
    private final InterruptController ic;
    private final DMAController dma;

    private PPU ppu;

    public void loadCartridge(Cartridge cartridge) { this.cart = cartridge; }

    public void removeCartridge() { this.cart = null; }

    public MemoryManagementUnit(InterruptController ic) {
        this.vram = new VRAM();
        this.wram = new WRAM();
        this.oam = new OAM();
        this.io = new IO();
        this.hram = new HRAM();
        this.ic = ic;
        this.cart = null;
        this.dma = new DMAController(oam);
        this.ppu = null;

        this.io.setInterrupts(ic);

        this.dma.setMemory(this::readDirect);
    }

    public void connectTimer(Timer timer) {
        io.setTimer(timer);
    }

    public void connectPpu(PPU ppu) {
        this.ppu = ppu;
        io.setPPU(ppu);
    }

    public void connectJoypad(JoyPad joypad) {
        io.setJoypad(joypad);
    }

    public void connectSerial(Serial serial) {
        io.setSerial(serial);
    }

    public void connectApu(Addressable apu) {
        io.setAPU(apu);
    }


    @Override
    public boolean accepts(int address) {
        return address >= 0x0000 && address <= 0xFFFF;
    }

    @Override
    public byte read(int address) {
        address &= 0xFFFF;

        if (isDMABlocked(address)) {
            return (byte) 0xFF;
        }

        if (address <= MemoryConstants.ROM_BANK_N_END) {
            return cart != null ? cart.read(address) : (byte) 0xFF;
        }

        if (address <= MemoryConstants.VRAM_END) {
            if (!isVRAMAccessible()) {
                return (byte) 0xFF;
            }
            return vram.read(address);
        }

        if (address <= MemoryConstants.ERAM_END) {
            return cart != null ? cart.read(address) : (byte) 0xFF;
        }

        if (address <= MemoryConstants.WRAM_END) {
            return wram.read(address);
        }

        if (address <= MemoryConstants.ECHO_END) {
            return wram.read(address - 0x2000);
        }

        if (address <= MemoryConstants.OAM_END) {
            if (!isOAMAccessible()) {
                return (byte) 0xFF;
            }
            return oam.read(address);
        }

        if (address <= MemoryConstants.UNUSED_END) {
            return (byte) 0xFF;
        }

        if (address <= MemoryConstants.IO_END) {
            if (address == MemoryConstants.DMA) {
                return (byte) dma.read();
            }
            return io.read(address);
        }

        if (address <= MemoryConstants.HRAM_END) {
            return hram.read(address);
        }

        return (byte) ic.getIE();
    }

    @Override
    public void write(int address, int value) {
        address &= 0xFFFF;
        value &= 0xFF;

        if (address <= MemoryConstants.ROM_BANK_N_END) {
            if (cart != null) {
                cart.write(address, value);
            }
            return;
        }

        if (address <= MemoryConstants.VRAM_END) {
            vram.write(address, value);
            return;
        }

        if (address <= MemoryConstants.ERAM_END) {
            if (cart != null) {
                cart.write(address, value);
            }
            return;
        }

        if (address <= MemoryConstants.WRAM_END) {
            wram.write(address, value);
            return;
        }

        if (address <= MemoryConstants.ECHO_END) {
            wram.write(address - 0x2000, value);
            return;
        }

        if (address <= MemoryConstants.OAM_END) {
            oam.write(address, value);
            return;
        }

        if (address <= MemoryConstants.UNUSED_END) {
            return;
        }

        if (address <= MemoryConstants.IO_END) {
            if (address == MemoryConstants.DMA) {
                dma.write(value);
                return;
            }
            io.write(address, value);
            return;
        }

        if (address <= MemoryConstants.HRAM_END) {
            hram.write(address, value);
            return;
        }

        if (address == MemoryConstants.IE_ADDRESS) {
            ic.setIE(value);
        }
    }

    public VRAM getVRAM() {
        return vram;
    }

    public OAM getOAM() {
        return oam;
    }

    private boolean isVRAMAccessible() {
        if (ppu == null || !ppu.isLCDEnabled()) {
            return true;
        }
        return ppu.getMode() != PPUMode.DRAWING;
    }

    private boolean isOAMAccessible() {
        if (ppu == null || !ppu.isLCDEnabled()) {
            return true;
        }
        PPUMode mode = ppu.getMode();
        return mode == PPUMode.HBLANK || mode == PPUMode.VBLANK;
    }

    private boolean isDMABlocked(int address) {
        if (!dma.isActive()) {
            return false;
        }
        return address < MemoryConstants.HRAM_START || address > MemoryConstants.HRAM_END;
    }

    public byte readDirect(int address) {
        address &= 0xFFFF;

        if (address <= MemoryConstants.ROM_BANK_N_END) {
            return cart != null ? cart.read(address) : (byte) 0xFF;
        }
        if (address <= MemoryConstants.VRAM_END) {
            return vram.read(address);
        }
        if (address <= MemoryConstants.ERAM_END) {
            return cart != null ? cart.read(address) : (byte) 0xFF;
        }
        if (address <= MemoryConstants.WRAM_END) {
            return wram.read(address);
        }
        if (address <= MemoryConstants.ECHO_END) {
            return wram.read(address - 0x2000);
        }

        return (byte) 0xFF;
    }

    @Override
    public void reset() {
        vram.reset();
        wram.reset();
        oam.reset();
        io.reset();
        hram.reset();
    }

    public IO getIO() { return io; }
    public WRAM getWWRAM() { return wram; }
    public HRAM getHRAM() { return hram; }
    public DMAController getDMA() { return dma; }
    public Cartridge getCartridge() { return cart; }

    public boolean isDMAActive() { return dma.isActive(); }
}
