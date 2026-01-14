package cpu.alu.result;

public record ALUR16(int value, boolean halfCarry, boolean carry) {

    public static ALUR16 result16(int value, boolean halfCarry, boolean carry) {
        int masked = value & 0xFFFF;
        return new ALUR16(masked, halfCarry, carry);
    }

    public static ALUR16 add(int a, int b) {
        int value = a + b;
        boolean halfCarry = ((a & 0xFFF) + (b & 0xFFF)) > 0xFFF;
        boolean carry = value > 0xFFFF;
        return result16(value, halfCarry, carry);
    }

    public int highByte() { return (value >> 8) & 0xFF; }
    public int lowByte() { return value & 0xFF; }

}
