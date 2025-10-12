package cpu.cpu.alu.result;

public record ALUR8(int value, boolean zero, boolean subtract, boolean halfCarry, boolean carry) {

    public static ALUR8 result8(int value, boolean subtract, boolean halfCarry, boolean carry) {
        int maskedValue = value & 0xFF;
        return new ALUR8(maskedValue, maskedValue == 0, subtract, halfCarry, carry);
    }

    // this is a quirk of the SM83. This is for Signed Operations
    public static ALUR8 result16(int value, boolean subtract, boolean halfCarry, boolean carry) {
        int masked = value & 0xFFFF;
        return new ALUR8(masked, masked == 0, subtract, halfCarry, carry);
    }

    public static ALUR8 add(int value, boolean halfCarry, boolean carry) {
        return result8(value, false, halfCarry, carry);
    }

    public static ALUR8 sub(int value, boolean halfCarry, boolean carry) {
        return result8(value, true, halfCarry, carry);
    }

    public static ALUR8 logic(int value, boolean halfCarry) {
        return result8(value, false, halfCarry, false);
    }

    public static ALUR8 shift(int value, boolean carry) {
        return result8(value, false, false, carry);
    }

}
