package cpu.alu.result;

public record FLAGR(boolean zero, boolean subtract, boolean halfCarry) {

    public static FLAGR result(int value, boolean subtract, boolean halfCarry) {
        return new FLAGR((value & 0xFF) == 0, subtract, halfCarry);
    }

    public static FLAGR forBit(int value, int bit) {
        boolean isZero = ((value >> bit) & 1) == 0;
        return new FLAGR(isZero, false, true);
    }

}
