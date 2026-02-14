package mem;

public class MemoryConstants {

    public static final int ROM_BANK_0_START = 0x0000;
    public static final int ROM_BANK_0_END = 0x3FFF;

    public static final int ROM_BANK_N_START = 0x4000;
    public static final int ROM_BANK_N_END = 0x7FFF;

    public static final int VRAM_START = 0x8000;
    public static final int VRAM_END = 0x9FFF;
    public static final int VRAM_SIZE = 0x2000;

    public static final int ERAM_START = 0xA000;
    public static final int ERAM_END = 0xBFFF;

    public static final int WRAM_0_START = 0xC000;
    public static final int WRAM_0_END = 0xCFFF;

    public static final int WRAM_N_START = 0xD000;
    public static final int WRAM_N_END = 0xDFFF;

    public static final int WRAM_START = 0xC000;
    public static final int WRAM_END = 0xDFFF;
    public static final int WRAM_SIZE = 0x2000;

    public static final int ECHO_START = 0xE000;
    public static final int ECHO_END = 0xFDFF;

    public static final int OAM_START = 0xFE00;
    public static final int OAM_END = 0xFE9F;
    public static final int OAM_SIZE = 0xA0;

    public static final int UNUSED_START = 0xFEA0;
    public static final int UNUSED_END = 0xFEFF;

    public static final int IO_START = 0xFF00;
    public static final int IO_END = 0xFF7F;
    public static final int IO_SIZE = 0x80;

    public static final int HRAM_START = 0xFF80;
    public static final int HRAM_END = 0xFFFE;
    public static final int HRAM_SIZE = 0x7F;

    public static final int IE_ADDRESS = 0xFFFF;

    public static final int P1_JOYP = 0xFF00;
    public static final int SELECT_DPAD = 0x10;
    public static final int SELECT_BUTTONS = 0x20;

    public static final int SB = 0xFF01;
    public static final int SC = 0xFF02;

    public static final int DIV  = 0xFF04;
    public static final int TIMA = 0xFF05;
    public static final int TMA  = 0xFF06;
    public static final int TAC  = 0xFF07;

    public static final int TAC_ENABLED = 0x04;
    public static final int TAC_CLOCK_MASK = 0x03;

    public static final int[] CLOCK_BITS = { 9, 3, 5, 7 };

    public static final int IF = 0xFF0F;

    public static final int APU_START = 0xFF10;
    public static final int APU_END   = 0xFF3F;

    public static final int LCDC = 0xFF40;
    public static final int STAT = 0xFF41;
    public static final int SCY  = 0xFF42;
    public static final int SCX  = 0xFF43;
    public static final int LY   = 0xFF44;
    public static final int LYC  = 0xFF45;
    public static final int DMA  = 0xFF46;
    public static final int BGP  = 0xFF47;
    public static final int OBP0 = 0xFF48;
    public static final int OBP1 = 0xFF49;
    public static final int WY   = 0xFF4A;
    public static final int WX   = 0xFF4B;

    public static final int DMA_LENGTH = 160;
    public static final int CYCLES_PER_BYTE = 4;
    public static final int TOTAL_CYCLES = 640;

}
