package cpu.components;

import shared.Component;

public class FlagRegister implements Component {

    private static final int ZERO_FLAG = 0x80;      // Bit 7
    private static final int SUBTRACT_FLAG = 0x40;  // Bit 6
    private static final int HALF_CARRY_FLAG = 0x20; // Bit 5
    private static final int CARRY_FLAG = 0x10;     // Bit 4

    private byte flags;

    public FlagRegister() {
        reset();
    }

    @Override
    public void tick(int cycles) {
        // no
    }

    @Override
    public void reset() {
        flags = (byte) 0xB0; // this should be the initial state for dmg
    }

    @Override
    public String getComponentName() {
        return "Flag Register";
    }

    public byte getValue() {
        return (byte) (flags & 0xF0); // Lower always 0
    }

    public void setValue(byte value) {
        flags = (byte) (value & 0xF0); // Lower always 0
    }

    // Zero flag
    public boolean getZero() {
        return (flags & ZERO_FLAG) != 0;
    }

    public void setZero(boolean value) {
        if (value) {
            flags |= ZERO_FLAG;
        } else {
            flags &= ~ZERO_FLAG;
        }
    }

    // Subtract flag
    public boolean getSubtract() {
        return (flags & SUBTRACT_FLAG) != 0;
    }

    public void setSubtract(boolean value) {
        if (value) {
            flags |= SUBTRACT_FLAG;
        } else {
            flags &= ~SUBTRACT_FLAG;
        }
    }

    // Half-carry flag
    public boolean getHalfCarry() {
        return (flags & HALF_CARRY_FLAG) != 0;
    }

    public void setHalfCarry(boolean value) {
        if (value) {
            flags |= HALF_CARRY_FLAG;
        } else {
            flags &= ~HALF_CARRY_FLAG;
        }
    }

    // Carry flag
    public boolean getCarry() {
        return (flags & CARRY_FLAG) != 0;
    }

    public void setCarry(boolean value) {
        if (value) {
            flags |= CARRY_FLAG;
        } else {
            flags &= ~CARRY_FLAG;
        }
    }

    public void setAll(boolean zero, boolean subtract, boolean halfCarry, boolean carry) {
        setZero(zero);
        setSubtract(subtract);
        setHalfCarry(halfCarry);
        setCarry(carry);
    }

}
