package cpu.constants;

public class CPUConstants {
    public static final int MASTER_CLOCK_HZ = 4194304;
    public static final int MASTER_CLOCK_GBC_HZ = 8388608;

    public static final int SYSTEM_CLOCK_HZ = MASTER_CLOCK_HZ / 4;
    public static final int SYSTEM_CLOCK_GBC_HZ = MASTER_CLOCK_GBC_HZ / 4;

    public static final int T_CYCLES_PER_M_CYCLE = 4;

    public static final int M_CYCLES_PER_FRAME = 17556;
    public static final int T_CYCLES_PER_FRAME = 70224;

    public static final double FRAMES_PER_SECOND = (double) MASTER_CLOCK_HZ / T_CYCLES_PER_FRAME;

    public static final int INTERRUPT_DISPATCH_M_CYCLES = 5;
    public static final int INTERRUPT_DISPATCH_T_CYCLES = INTERRUPT_DISPATCH_M_CYCLES * T_CYCLES_PER_M_CYCLE;


    public static final int VBLANK_VECTOR = 0x0040;
    public static final int LCD_STAT_VECTOR = 0x0048;
    public static final int TIMER_VECTOR = 0x0050;
    public static final int SERIAL_VECTOR = 0x0058;
    public static final int JOYPAD_VECTOR = 0x0060;

    public static final int IE_ADDRESS = 0xFFFF;
    public static final int IF_ADDRESS = 0xFF0F;

    public static final int ENTRY_POINT = 0x0100;

    public static final int[] RST_VECTOR = {
            0x0000, 0x0008, 0x0010, 0x0018,
            0x0020, 0x0028, 0x0030, 0x0038
    };

    public static final class InitialValues {

        private InitialValues() {
        }

        public static final int A = 0x01;
        public static final int F = 0xB0;
        public static final int B = 0x00;
        public static final int C = 0x13;
        public static final int D = 0x00;
        public static final int E = 0xD8;
        public static final int H = 0x01;
        public static final int L = 0x4D;
        public static final int SP = 0xFFFE;
        public static final int PC = ENTRY_POINT;

        public static final int AF = (A << 8) | F;
        public static final int BC = (B << 8) | C;
        public static final int DE = (D << 8) | E;
        public static final int HL = (H << 8) | L;

    }

    public static final int INTERRUPT_MASK = 0x1F;
    public static final int IF_UNUSED_BITS = 0xE0;
    public static final int BYTE_MASK = 0xFF;
    public static final int WORD_MASK = 0xFFFF;
    public static final int NIBBLE_MASK = 0x0F;
    public static final int FLAG_MASK = 0xF0;

}
