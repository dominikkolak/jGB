package cart.header.enums;

public enum SGBSupport {

    UNSUPPORTED(0x00),
    SUPPORTED(0x03);

    public final int value;

    SGBSupport(int value) {
        this.value = value;
    }

    public static SGBSupport fromByte(int b) {
        return b == 0x03 ? SUPPORTED : UNSUPPORTED;
    }

    public boolean isSupported() {
        return this == SUPPORTED;
    }

}
