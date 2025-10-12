package cpu.components;

import shared.Component;

public class ALU implements Component {

    private final FlagRegister flags;

    public ALU(FlagRegister flags) {
        this.flags = flags;
    }

    @Override
    public void tick(int cycles) {
        // alu is always working no need for sync
    }

    @Override
    public void reset() {
        // no state
    }

    @Override
    public String getComponentName() {
        return "ALU";
    }

    // 8-bit Ari Op
    public byte add(byte a, byte b, boolean useCarry) {
        int carry = (useCarry && flags.getCarry()) ? 1 : 0;
        int result = (a & 0xFF) + (b & 0xFF) + carry;

        flags.setZero((result & 0xFF) == 0);
        flags.setSubtract(false);
        flags.setHalfCarry(((a & 0x0F) + (b & 0x0F) + carry) > 0x0F);
        flags.setCarry(result > 0xFF);

        return (byte) result;
    }

    public byte sub(byte a, byte b, boolean useCarry) {
        int carry = (useCarry && flags.getCarry()) ? 1 : 0;
        int result = (a & 0xFF) - (b & 0xFF) - carry;

        flags.setZero((result & 0xFF) == 0);
        flags.setSubtract(true);
        flags.setHalfCarry(((a & 0x0F) - (b & 0x0F) - carry) < 0);
        flags.setCarry(result < 0);

        return (byte) result;
    }

    public byte and(byte a, byte b) {
        byte result = (byte) (a & b);

        flags.setZero(result == 0);
        flags.setSubtract(false);
        flags.setHalfCarry(true);
        flags.setCarry(false);

        return result;
    }

    public byte or(byte a, byte b) {
        byte result = (byte) (a | b);

        flags.setZero(result == 0);
        flags.setSubtract(false);
        flags.setHalfCarry(false);
        flags.setCarry(false);

        return result;
    }

    public byte xor(byte a, byte b) {
        byte result = (byte) (a ^ b);

        flags.setZero(result == 0);
        flags.setSubtract(false);
        flags.setHalfCarry(false);
        flags.setCarry(false);

        return result;
    }

    public void compare(byte a, byte b) {
        sub(a, b, false); // compare subtract without storing result!!!!!!!!!!!!!!!
    }

    public byte increment(byte value) {
        byte result = (byte) ((value & 0xFF) + 1);

        flags.setZero(result == 0);
        flags.setSubtract(false);
        flags.setHalfCarry((value & 0x0F) == 0x0F);
        // Carry flag not affected

        return result;
    }

    public byte decrement(byte value) {
        byte result = (byte) ((value & 0xFF) - 1);

        flags.setZero(result == 0);
        flags.setSubtract(true);
        flags.setHalfCarry((value & 0x0F) == 0);
        // Carry flag not affected

        return result;
    }

    // Rot and Shi Op
    public byte rotateLeftCircular(byte value) {
        int bit7 = (value & 0x80) >> 7;
        byte result = (byte) ((value << 1) | bit7);

        flags.setZero(false); // RLCA always clears zero flag? Need to check
        flags.setSubtract(false);
        flags.setHalfCarry(false);
        flags.setCarry(bit7 != 0);

        return result;
    }

    public byte rotateLeft(byte value) {
        int bit7 = (value & 0x80) >> 7;
        int carryIn = flags.getCarry() ? 1 : 0;
        byte result = (byte) ((value << 1) | carryIn);

        flags.setZero(false); // RLA always clears zero flag?
        flags.setSubtract(false);
        flags.setHalfCarry(false);
        flags.setCarry(bit7 != 0);

        return result;
    }

    public byte rotateRightCircular(byte value) {
        int bit0 = value & 0x01;
        byte result = (byte) (((value & 0xFF) >> 1) | (bit0 << 7));

        flags.setZero(false); // RRCA always clears zero flag? Maybe MBC variants?????????
        flags.setSubtract(false);
        flags.setHalfCarry(false);
        flags.setCarry(bit0 != 0);

        return result;
    }

    public byte rotateRight(byte value) {
        int bit0 = value & 0x01;
        int carryIn = flags.getCarry() ? 0x80 : 0;
        byte result = (byte) (((value & 0xFF) >> 1) | carryIn);

        flags.setZero(false); // RRA always clears zero flag?
        flags.setSubtract(false);
        flags.setHalfCarry(false);
        flags.setCarry(bit0 != 0);

        return result;
    }

    public byte swap(byte value) {
        byte result = (byte) (((value & 0x0F) << 4) | ((value & 0xF0) >> 4));

        flags.setZero(result == 0);
        flags.setSubtract(false);
        flags.setHalfCarry(false);
        flags.setCarry(false);

        return result;
    }

    // Bit Op
    public void testBit(byte value, int bit) {
        boolean isSet = ((value >> bit) & 0x01) != 0;

        flags.setZero(!isSet);
        flags.setSubtract(false);
        flags.setHalfCarry(true);
        // Carry flag not affected
    }

    public byte setBit(byte value, int bit) {
        return (byte) (value | (1 << bit));
    }

    public byte resetBit(byte value, int bit) {
        return (byte) (value & ~(1 << bit));
    }

    // 16-bit operations
    public int add16(int a, int b) {
        int result = (a + b) & 0xFFFF;

        flags.setSubtract(false);
        flags.setHalfCarry(((a & 0x0FFF) + (b & 0x0FFF)) > 0x0FFF);
        flags.setCarry((a + b) > 0xFFFF); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        return result;
    }

    public int increment16(int value) {
        return (value + 1) & 0xFFFF;
    }

    public int decrement16(int value) {
        return (value - 1) & 0xFFFF;
    }

}
