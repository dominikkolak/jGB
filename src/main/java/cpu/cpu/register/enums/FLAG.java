package cpu.cpu.register.enums;

public enum FLAG {
    ZERO(7, 0x80),
    SUBTRACT(6, 0x40),
    HALF_CARRY(5, 0x20),
    CARRY(4, 0x10);

    private final int bit;
    private final int mask;

    FLAG(int bit, int mask) {
        this.bit = bit;
        this.mask = mask;
    }

    public int bit() { return bit; }
    public int mask() { return mask; }

    public boolean isSet(int flagRegister) { return (flagRegister & mask) != 0; }
    public int set(int flagRegister) { return flagRegister | mask; }

    public int clear(int flagRegister) { return flagRegister & ~mask; }
    public int apply(int flagRegister, boolean condition) { return condition ? set(flagRegister) : clear(flagRegister); }
}
