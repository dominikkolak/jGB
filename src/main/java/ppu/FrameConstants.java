package ppu;

public class FrameConstants {

    public static final int WIDTH = 160;
    public static final int HEIGHT = 144;

    public static final int TOTAL_SCANLINES = 154;
    public static final int DOTS_PER_SCANLINE = 456;

    public static final int OAM_SCAN_DOTS = 80;
    public static final int DRAWING_MIN_DOTS = 172;

    public static final int LCDC_ENABLE       = 0x80;
    public static final int LCDC_WIN_TILEMAP  = 0x40;
    public static final int LCDC_WIN_ENABLE   = 0x20;
    public static final int LCDC_TILE_DATA    = 0x10;
    public static final int LCDC_BG_TILEMAP   = 0x08;
    public static final int LCDC_OBJ_SIZE     = 0x04;
    public static final int LCDC_OBJ_ENABLE   = 0x02;
    public static final int LCDC_BG_ENABLE    = 0x01;

    public static final int STAT_LYC_INT      = 0x40;
    public static final int STAT_MODE2_INT    = 0x20;
    public static final int STAT_MODE1_INT    = 0x10;
    public static final int STAT_MODE0_INT    = 0x08;

}
