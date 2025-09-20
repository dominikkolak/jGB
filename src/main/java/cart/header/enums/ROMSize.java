package cart.header.enums;

public enum ROMSize {

    KB_32(0x00, 32768, 2),
    KB_64(0x01, 65536, 4),
    KB_128(0x02, 131072, 8),
    KB_256(0x03, 262144, 16),
    KB_512(0x04, 524288, 32),
    MB_1(0x05, 1048576, 64),
    MB_2(0x06, 2097152, 128),
    MB_4(0x07, 4194304, 256),
    MB_8(0x08, 8388608, 512),

    MB_1_1(0x52, 1179648, 72),
    MB_1_2(0x53, 1310720, 80),
    MB_1_5(0x54, 1572864, 96);

    public final int value;
    public final int sizeInBytes;
    public final int bankCount;

    ROMSize(int value, int sizeInBytes, int bankCount) {
        this.value = value;
        this.sizeInBytes = sizeInBytes;
        this.bankCount = bankCount;
    }

    public static ROMSize fromByte(int b) {
        for (ROMSize size : values()) {
            if (size.value == b) {
                return size;
            }
        }
        throw new IllegalArgumentException("Unknown ROM size");
    }

}
