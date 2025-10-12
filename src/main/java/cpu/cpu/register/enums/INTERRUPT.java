package cpu.cpu.register.enums;

public enum INTERRUPT {
    VBLANK(0, 0x0040),
    LCD_STAT(1, 0x0048),
    TIMER(2, 0x0050),
    SERIAL(3, 0x0058),
    JOYPAD(4, 0x0060);

    private final int bit;
    private final int vector;

    INTERRUPT(int bit, int vector) {
        this.bit = bit;
        this.vector = vector;
    }

    public int bit() { return bit; }
    public int mask() { return 1 << bit; }
    public int vector() { return vector; }

    public boolean isEnabled(int ieRegister) { return (ieRegister & mask()) != 0; }
    public boolean isPending(int ifRegister) { return (ifRegister & mask()) != 0; }

    public boolean shouldService(int ieRegister, int ifRegister) {
        int mask = mask();
        return (ieRegister & mask) != 0 && (ifRegister & mask) != 0;
    }

    public static INTERRUPT fromBit(int bit) {
        return switch (bit) {
            case 0 -> VBLANK;
            case 1 -> LCD_STAT;
            case 2 -> TIMER;
            case 3 -> SERIAL;
            case 4 -> JOYPAD;
            default -> throw new IllegalArgumentException("Invalid interrupt bit");
        };
    }

    public static INTERRUPT getHighestPriority(int ieRegister, int ifRegister) {
        int pending = ieRegister & ifRegister & 0x1F;
        if (pending == 0) { return null; }
        int bit = Integer.numberOfTrailingZeros(pending);
        return fromBit(bit);
    }

}
