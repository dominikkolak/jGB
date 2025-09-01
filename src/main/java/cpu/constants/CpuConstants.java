package cpu.constants;

public class CpuConstants {

    // Clock speeds
    // WARNING: Real Clock speeds very on hardware. Official Clocks are known to cause issues
    // This requires testing!!!
    public static final int CLOCK_SPEED_HZ = 4_194_304; // ~4.19 MHz
    public static final int CYCLES_PER_FRAME = 70224; // For 59.7 FPS

    // Register initial values for DMG boot sequence final states
    public static final int INITIAL_AF = 0x01B0;
    public static final int INITIAL_BC = 0x0013;
    public static final int INITIAL_DE = 0x00D8;
    public static final int INITIAL_HL = 0x014D;
    public static final int INITIAL_SP = 0xFFFE;
    public static final int INITIAL_PC = 0x0100;

    // Memory addresses
    public static final int ROM_START = 0x0000;
    public static final int ROM_END = 0x7FFF;
    public static final int VRAM_START = 0x8000;
    public static final int VRAM_END = 0x9FFF;
    public static final int EXTERNAL_RAM_START = 0xA000;
    public static final int EXTERNAL_RAM_END = 0xBFFF;
    public static final int WORK_RAM_START = 0xC000;
    public static final int WORK_RAM_END = 0xDFFF;
    public static final int ECHO_RAM_START = 0xE000;
    public static final int ECHO_RAM_END = 0xFDFF;
    public static final int OAM_START = 0xFE00;
    public static final int OAM_END = 0xFE9F;
    public static final int IO_START = 0xFF00;
    public static final int IO_END = 0xFF7F;
    public static final int HRAM_START = 0xFF80;
    public static final int HRAM_END = 0xFFFE;
    public static final int IE_REGISTER = 0xFFFF;

    private CpuConstants() {}

}
