package cart.header.enums;

public enum CartridgeType {

    ROM_ONLY(0x00, false, false, false, false),

    MBC1(0x01, false, false, false, false),
    MBC1_RAM(0x02, true, false, false, false),
    MBC1_RAM_BATTERY(0x03, true, true, false, false),

    MBC2(0x05, true, false, false, false),
    MBC2_BATTERY(0x06, true, true, false, false),

    ROM_RAM(0x08, true, false, false, false),
    ROM_RAM_BATTERY(0x09, true, true, false, false),

    MMM01(0x0B, false, false, false, false),
    MMM01_RAM(0x0C, true, false, false, false),
    MMM01_RAM_BATTERY(0x0D, true, true, false, false),

    MBC3_TIMER_BATTERY(0x0F, false, true, true, false),
    MBC3_TIMER_RAM_BATTERY(0x10, true, true, true, false),
    MBC3(0x11, false, false, false, false),
    MBC3_RAM(0x12, true, false, false, false),
    MBC3_RAM_BATTERY(0x13, true, true, false, false),

    MBC5(0x19, false, false, false, false),
    MBC5_RAM(0x1A, true, false, false, false),
    MBC5_RAM_BATTERY(0x1B, true, true, false, false),
    MBC5_RUMBLE(0x1C, false, false, false, true),
    MBC5_RUMBLE_RAM(0x1D, true, false, false, true),
    MBC5_RUMBLE_RAM_BATTERY(0x1E, true, true, false, true),

    MBC6(0x20, true, true, false, false),

    MBC7_SENSOR_RUMBLE_RAM_BATTERY(0x22, true, true, false, true),

    // won't actually implement but here just for the sake of completeness
    POCKET_CAMERA(0xFC, true, true, false, false),
    TAMA5(0xFD, false, true, true, false),
    HUC3(0xFE, true, true, true, false),
    HUC1_RAM_BATTERY(0xFF, true, true, false, false);

    public final int value;
    public final boolean hasRAM;
    public final boolean hasBattery;
    public final boolean hasRTC;
    public final boolean hasRumble;

    CartridgeType(int value, boolean hasRAM, boolean hasBattery, boolean hasRTC, boolean hasRumble) {
        this.value = value;
        this.hasRAM = hasRAM;
        this.hasBattery = hasBattery;
        this.hasRTC = hasRTC;
        this.hasRumble = hasRumble;
    }

    public static CartridgeType fromByte(int b) {
        for (CartridgeType type : values()) {
            if (type.value == b) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Cartridge Type");
    }

    public String getMBCName() {
        return switch (this) {
            case ROM_ONLY, ROM_RAM, ROM_RAM_BATTERY -> "None";
            case MBC1, MBC1_RAM, MBC1_RAM_BATTERY -> "MBC1";
            case MBC2, MBC2_BATTERY -> "MBC2";
            case MMM01, MMM01_RAM, MMM01_RAM_BATTERY -> "MMM01";
            case MBC3, MBC3_RAM, MBC3_RAM_BATTERY,
                 MBC3_TIMER_BATTERY, MBC3_TIMER_RAM_BATTERY -> "MBC3";
            case MBC5, MBC5_RAM, MBC5_RAM_BATTERY,
                 MBC5_RUMBLE, MBC5_RUMBLE_RAM, MBC5_RUMBLE_RAM_BATTERY -> "MBC5";
            case MBC6 -> "MBC6";
            case MBC7_SENSOR_RUMBLE_RAM_BATTERY -> "MBC7";
            case HUC1_RAM_BATTERY -> "HuC1";
            case HUC3 -> "HuC3";
            case TAMA5 -> "TAMA5";
            case POCKET_CAMERA -> "Camera";
        };
    }

}
