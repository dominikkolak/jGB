package cpu.register;

import cpu.alu.result.ALUR8;
import cpu.constants.CPUConstants;
import cpu.register.enums.CONDITION;
import cpu.register.enums.FLAG;
import cpu.register.enums.R16;
import cpu.register.enums.R8;
import shared.Component;

public class RegisterFile implements Registers, FlagOperations, Component {

    private int a, b, c, d, e, h, l;
    private int f;

    private int sp;
    private int pc;

    public RegisterFile() {
        reset();
    }


    @Override
    public boolean getFlag(FLAG flag) {
        return (f & flag.mask()) != 0;
    }

    @Override
    public void setFlag(FLAG flag, boolean value) {
        if (value) {
            f |= flag.mask();
        } else {
            f &= ~flag.mask();
        }
    }

    @Override
    public void setAllFlags(boolean z, boolean n, boolean h, boolean c) {
        f = (z ? FLAG.ZERO.mask() : 0)
                | (n ? FLAG.SUBTRACT.mask() : 0)
                | (h ? FLAG.HALF_CARRY.mask() : 0)
                | (c ? FLAG.CARRY.mask() : 0);
    }

    @Override
    public void setFlags(Boolean z, Boolean n, Boolean h, Boolean c) {
        if (z != null) {
            setFlag(FLAG.ZERO, z);
        }
        if (n != null) {
            setFlag(FLAG.SUBTRACT, n);
        }
        if (h != null) {
            setFlag(FLAG.HALF_CARRY, h);
        }
        if (c != null) {
            setFlag(FLAG.CARRY, c);
        }
    }

    @Override
    public void setFlags(ALUR8 result) {
        setFlags(result.zero(), result.subtract(), result.halfCarry(), result.carry());
    }

    @Override
    public int getFlagRegister() {
        return f;
    }

    @Override
    public void setFlagRegister(int value) {
        f = value & 0xF0;
    }

    @Override
    public boolean checkCondition(CONDITION condition) {
        return switch (condition) {
            case NZ -> !getFlag(FLAG.ZERO);
            case Z -> getFlag(FLAG.ZERO);
            case NC -> !getFlag(FLAG.CARRY);
            case C -> getFlag(FLAG.CARRY);
        };
    }

    @Override
    public int read(R8 register) {
        return switch (register) {
            case A -> a;
            case B -> b;
            case C -> c;
            case D -> d;
            case E -> e;
            case H -> h;
            case L -> l;
            case F -> f;
        };
    }

    @Override
    public void write(R8 register, int value) {
        value &= 0xFF;
        switch (register) {
            case A -> a = value;
            case B -> b = value;
            case C -> c = value;
            case D -> d = value;
            case E -> e = value;
            case H -> h = value;
            case L -> l = value;
            case F -> f = value & 0xF0;
        }
    }

    @Override
    public int read(R16 register) {
        return switch (register) {
            case AF -> (a << 8) | f;
            case BC -> (b << 8) | c;
            case DE -> (d << 8) | e;
            case HL -> (h << 8) | l;
            case SP -> sp;
            case PC -> pc;
        };
    }

    @Override
    public void write(R16 register, int value) {
        value &= 0xFFFF;
        switch (register) {
            case AF -> {
                a = (value >> 8) & 0xFF;
                f = value & 0xF0;
            }
            case BC -> {
                b = (value >> 8) & 0xFF;
                c = value & 0xFF;
            }
            case DE -> {
                d = (value >> 8) & 0xFF;
                e = value & 0xFF;
            }
            case HL -> {
                h = (value >> 8) & 0xFF;
                l = value & 0xFF;
            }
            case SP -> sp = value;
            case PC -> pc = value;
        }
    }

    @Override
    public int getPC() {
        return pc;
    }

    @Override
    public void setPC(int value) {
        pc = value & 0xFFFF;
    }

    @Override
    public int getAndIncPC() {
        int result = pc;
        pc = (pc + 1) & 0xFFFF;
        return result;
    }

    @Override
    public void addToPC(int offset) {
        pc = (pc + offset) & 0xFFFF;
    }

    @Override
    public int getSP() {
        return sp;
    }

    @Override
    public void setSP(int value) {
        sp = value & 0xFFFF;
    }

    @Override
    public int decAndGetSP() {
        sp = (sp - 1) & 0xFFFF;
        return sp;
    }

    @Override
    public int getAndIncSP() {
        int result = sp;
        sp = (sp + 1) & 0xFFFF;
        return result;
    }

    @Override
    public void addToSP(int offset) {
        sp = (sp + offset) & 0xFFFF;
    }

    @Override
    public void reset() {
        a = CPUConstants.InitialValues.A;
        f = CPUConstants.InitialValues.F;
        b = CPUConstants.InitialValues.B;
        c = CPUConstants.InitialValues.C;
        d = CPUConstants.InitialValues.D;
        e = CPUConstants.InitialValues.E;
        h = CPUConstants.InitialValues.H;
        l = CPUConstants.InitialValues.L;
        sp = CPUConstants.InitialValues.SP;
        pc = CPUConstants.InitialValues.PC;
    }


}
