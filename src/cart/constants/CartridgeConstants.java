package cart.constants;

public class CartridgeConstants {

    public static final int ROM_BANK_SIZE = 16384;
    public static final int RAM_BANK_SIZE = 8192;

    public static final int ROM_BANK_0_START = 0x0000;
    public static final int ROM_BANK_0_END = 0x3FFF;
    public static final int ROM_BANK_N_START = 0x4000;
    public static final int ROM_BANK_N_END = 0x7FFF;

    public static final int RAM_START = 0xA000;
    public static final int RAM_END = 0xBFFF;

    public static final int HEADER_START = 0x0100;
    public static final int HEADER_END = 0x014F;
    public static final int ENTRY_POINT = 0x0100;
    public static final int LOGO_START = 0x0104;
    public static final int LOGO_END = 0x0133;
    public static final int TITLE_START = 0x0134;
    public static final int TITLE_END = 0x0143;
    public static final int MANUFACTURER_START = 0x013F;
    public static final int CGB_FLAG = 0x0143;
    public static final int LICENSEE_NEW_START = 0x0144;
    public static final int SGB_FLAG = 0x0146;
    public static final int CARTRIDGE_TYPE = 0x0147;
    public static final int ROM_SIZE = 0x0148;
    public static final int RAM_SIZE = 0x0149;
    public static final int DESTINATION = 0x014A;
    public static final int LICENSEE_OLD = 0x014B;
    public static final int VERSION = 0x014C;
    public static final int HEADER_CHECKSUM = 0x014D;
    public static final int GLOBAL_CHECKSUM_HIGH = 0x014E;
    public static final int GLOBAL_CHECKSUM_LOW = 0x014F;

    public static final int RAM_ENABLE_VALUE = 0x0A;
    public static final int RAM_DISABLE_VALUE = 0x00;

    public static final byte[] NINTENDO_LOGO = {
            (byte)0xCE, (byte)0xED, (byte)0x66, (byte)0x66, (byte)0xCC, (byte)0x0D,
            (byte)0x00, (byte)0x0B, (byte)0x03, (byte)0x73, (byte)0x00, (byte)0x83,
            (byte)0x00, (byte)0x0C, (byte)0x00, (byte)0x0D, (byte)0x00, (byte)0x08,
            (byte)0x11, (byte)0x1F, (byte)0x88, (byte)0x89, (byte)0x00, (byte)0x0E,
            (byte)0xDC, (byte)0xCC, (byte)0x6E, (byte)0xE6, (byte)0xDD, (byte)0xDD,
            (byte)0xD9, (byte)0x99, (byte)0xBB, (byte)0xBB, (byte)0x67, (byte)0x63,
            (byte)0x6E, (byte)0x0E, (byte)0xEC, (byte)0xCC, (byte)0xDD, (byte)0xDC,
            (byte)0x99, (byte)0x9F, (byte)0xBB, (byte)0xB9, (byte)0x33, (byte)0x3E
    };

    public static final int MIN_ROM_SIZE = 0x8000;  // 32KB

}
