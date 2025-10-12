package cpu.cpu.register.enums;

import cpu.register.enums.FLAG;

public enum CONDITION {
    NZ, Z, NC, C;

    public boolean verify(boolean zFlag, boolean cFlag) {
        return switch (this) {
            case NZ -> !zFlag;
            case Z -> zFlag;
            case NC -> !cFlag;
            case C -> cFlag;
        };
    }

    public boolean verifyDirect(int flagRegister) {
        boolean zFlag = cpu.register.enums.FLAG.ZERO.isSet(flagRegister);
        boolean cFlag = FLAG.CARRY.isSet(flagRegister);
        return verify(zFlag, cFlag);
    }

    public static CONDITION fromBits(int bits) {
        return switch (bits) {
            case 0 -> NZ;
            case 1 -> Z;
            case 2 -> NC;
            case 3 -> C;
            default -> throw new IllegalArgumentException("Invalid Condition Bits");
        };
    }
}
