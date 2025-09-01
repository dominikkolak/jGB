package cpu.constants;

public class InterruptVectors {

    // This isnt all that relevant for now since PPU and IO isnt part of this module

    // Interrupt vector addresses
    public static final int VBLANK_VECTOR = 0x0040;
    public static final int LCD_STAT_VECTOR = 0x0048;
    public static final int TIMER_VECTOR = 0x0050;
    public static final int SERIAL_VECTOR = 0x0058;
    public static final int JOYPAD_VECTOR = 0x0060;

    // Interrupt bit positions in IF and IE registers
    public static final int VBLANK_BIT = 0;
    public static final int LCD_STAT_BIT = 1;
    public static final int TIMER_BIT = 2;
    public static final int SERIAL_BIT = 3;
    public static final int JOYPAD_BIT = 4;

    // Interrupt register addresses
    public static final int IF_ADDRESS = 0xFF0F; // Interrupt Flag
    public static final int IE_ADDRESS = 0xFFFF; // Interrupt Enable

    private InterruptVectors() {}
}
