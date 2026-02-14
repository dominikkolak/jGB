package cart.header.enums;

public enum Destination {

    // might be related to the boot sequence but since we skip it this isn't a factor
    // maybe some kind of drm

    JAPAN(0x00),
    INTERNATIONAL(0x01);

    public final int value;

    Destination(int value) {
        this.value = value;
    }

    public static Destination fromByte(int b) {
        return b == 0x00 ? JAPAN : INTERNATIONAL;
    }

}
