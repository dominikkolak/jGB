package cart.header.enums;

public enum CGBSupport {

    NONE(0x00),         // DMG only
    COMPATIBLE(0x80),   // DMG and CGB
    EXCLUSIVE(0xC0);    // CGB only

    public final int value;

    CGBSupport(int value) {
        this.value = value;
    }

    public static CGBSupport fromByte(int b) {
        return switch (b & 0xC0) {
            case 0x80 -> COMPATIBLE;
            case 0xC0 -> EXCLUSIVE;
            default -> NONE;
        };
    }

    public boolean isSupported() {
        return this != NONE;
    }

    public boolean isExclusive() {
        return this == EXCLUSIVE;
    }


}
