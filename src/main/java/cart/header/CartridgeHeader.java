package cart.header;

import cart.constants.CartridgeConstants;
import cart.exceptions.InvalidCartridgeException;
import cart.header.enums.*;

import java.util.Arrays;

public record CartridgeHeader(
        byte[] entryPoint,
        byte[] nintendoLogo,
        String title,
        String manufacturerCode,
        CGBSupport cgbSupport,
        String newLicenseeCode,
        SGBSupport sgbSupport,
        CartridgeType cartridgeType,
        ROMSize romSize,
        RAMSize ramSize,
        Destination destination,
        int oldLicenseeCode,
        int versionNumber,
        int headerChecksum,
        int globalChecksum
) {

    private static byte[] extractBytes(byte[] data, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(data, offset, result, 0, length);
        return result;
    }

    private static String extractString(byte[] data, int offset, int max_length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < max_length; i++) {
            byte b = data[offset + i];
            if (b == 0) break;
            if (b >= 0x20 && b <= 0x7E) {  // ASCII
                sb.append((char) b);
            }
        }
        return sb.toString().trim();
    }

    public static CartridgeHeader parse(byte[] romData) {
        if (romData == null || romData.length < CartridgeConstants.HEADER_END + 1) { throw new IllegalArgumentException("ROM too small to contain header"); }

        byte[] entryPointR = extractBytes(romData, CartridgeConstants.ENTRY_POINT, 4);
        byte[] nintendoLogoR = extractBytes(romData, CartridgeConstants.LOGO_START, 48);

        int cgbByte = Byte.toUnsignedInt(romData[CartridgeConstants.CGB_FLAG]);
        CGBSupport cgbSupportR = CGBSupport.fromByte(cgbByte); // decides title length

        String titleR;
        String manufacturerCodeR;

        if (cgbSupportR.isSupported()) {
            titleR = extractString(romData, CartridgeConstants.TITLE_START, 11);
            manufacturerCodeR = extractString(romData, CartridgeConstants.MANUFACTURER_START, 4);
        } else {
            titleR = extractString(romData, CartridgeConstants.TITLE_START, 15);
            manufacturerCodeR = "";
        }

        String newLicenseeCodeR = extractString(romData, CartridgeConstants.LICENSEE_NEW_START, 2);
        SGBSupport sgbSupportR = SGBSupport.fromByte(Byte.toUnsignedInt(romData[CartridgeConstants.SGB_FLAG]));
        CartridgeType cartridgeTypeR = CartridgeType.fromByte(Byte.toUnsignedInt(romData[CartridgeConstants.CARTRIDGE_TYPE]));
        ROMSize romSizeR = ROMSize.fromByte(Byte.toUnsignedInt(romData[CartridgeConstants.ROM_SIZE]));
        RAMSize ramSizeR = RAMSize.fromByte(Byte.toUnsignedInt(romData[CartridgeConstants.RAM_SIZE]));
        Destination destinationR = Destination.fromByte(Byte.toUnsignedInt(romData[CartridgeConstants.DESTINATION]));
        int oldLicenseeCodeR = Byte.toUnsignedInt(romData[CartridgeConstants.LICENSEE_OLD]);
        int versionNumberR = Byte.toUnsignedInt(romData[CartridgeConstants.VERSION]);
        int headerChecksumR = Byte.toUnsignedInt(romData[CartridgeConstants.HEADER_CHECKSUM]);
        int globalChecksumR = (Byte.toUnsignedInt(romData[CartridgeConstants.GLOBAL_CHECKSUM_HIGH]) << 8) | Byte.toUnsignedInt(romData[CartridgeConstants.GLOBAL_CHECKSUM_LOW]);

        if (ramSizeR.sizeInBytes % CartridgeConstants.RAM_BANK_SIZE != 0) { throw new InvalidCartridgeException("RAM Size Mismatch"); }

        return new CartridgeHeader(
                entryPointR,
                nintendoLogoR,
                titleR,
                manufacturerCodeR,
                cgbSupportR,
                newLicenseeCodeR,
                sgbSupportR,
                cartridgeTypeR,
                romSizeR,
                ramSizeR,
                destinationR,
                oldLicenseeCodeR,
                versionNumberR,
                headerChecksumR,
                globalChecksumR
        );
    }

    public boolean hasBattery() { return cartridgeType.hasBattery; }

    public boolean hasRTC() { return cartridgeType.hasRTC; }

    public boolean hasRumble() { return cartridgeType.hasRumble; }

    public boolean hasRAM() { return cartridgeType.hasRAM || ramSize.sizeInBytes > 0; }

    public boolean supportsCGB() { return cgbSupport.isSupported(); }

    public boolean isCGBOnly() { return cgbSupport.isExclusive(); }

    public boolean supportsSGB() { return sgbSupport.isSupported(); }

    public String getRegion() { return destination == Destination.JAPAN ? "Japan" : "International"; }

    public String getLicenseeCode() {
        if (oldLicenseeCode == 0x33) {
            return newLicenseeCode;
        }
        return String.format("%02X", oldLicenseeCode);
    }

    public boolean isHeaderChecksumValid(byte[] rom_data) {
        int calculated = calculateHeaderChecksum(rom_data);
        return calculated == headerChecksum;
    }

    private int calculateHeaderChecksum(byte[] rom_data) {
        int checksum = 0;
        for (int addr = CartridgeConstants.TITLE_START; addr <= CartridgeConstants.VERSION; addr++) {
            checksum = (checksum - Byte.toUnsignedInt(rom_data[addr]) - 1) & 0xFF;
        }
        return checksum;
    }

    public boolean isNintendoLogoValid() {
        return Arrays.equals(nintendoLogo, CartridgeConstants.NINTENDO_LOGO);
    }

    public boolean isCartridgeValid(byte[] rom_data) {
        return isHeaderChecksumValid(rom_data) && isNintendoLogoValid();
    }

}
