package cpu.components;

import cpu.exceptions.InvalidRegisterException;
import cpu.instructions.enums.Register16;
import cpu.instructions.enums.Register8;
import shared.Component;

public class RegisterFile implements Component {

    // 8-bit
    private byte a; // Accumulator
    private byte b;
    private byte c;
    private byte d;
    private byte e;
    private byte h;
    private byte l;

    // Special
    private final FlagRegister flags;
    private final StackPointer sp;
    private final ProgramCounter pc;

    public RegisterFile() {
        this.flags = new FlagRegister();
        this.sp = new StackPointer();
        this.pc = new ProgramCounter();
        reset();
    }

    @Override
    public void tick(int cycles) {
        // no
    }

    @Override
    public void reset() {
        // DMG initial state after boot ROM !!!!!!!!!!!!!
        // NOT BEFORE ROM !!!!!!!
        a = 0x01;
        flags.setValue((byte) 0xB0);
        b = 0x00;
        c = 0x13;
        d = 0x00;
        e = (byte) 0xD8;
        h = 0x01;
        l = 0x4D;
        sp.reset();
        pc.reset();
    }

    @Override
    public String getComponentName() {
        return "Register File";
    }

    // 8-bit ra
    public byte read8(Register8 register) {
        switch (register) {
            case A: return a;
            case F: return flags.getValue();
            case B: return b;
            case C: return c;
            case D: return d;
            case E: return e;
            case H: return h;
            case L: return l;
            default: throw new InvalidRegisterException(register.toString());
        }
    }

    public void write8(Register8 register, byte value) {
        switch (register) {
            case A: a = value; break;
            case F: flags.setValue(value); break;
            case B: b = value; break;
            case C: c = value; break;
            case D: d = value; break;
            case E: e = value; break;
            case H: h = value; break;
            case L: l = value; break;
            default: throw new InvalidRegisterException(register.toString());
        }
    }

    // 16-bit ra pair
    public int read16(Register16 register) {
        switch (register) {
            case AF: return ((a & 0xFF) << 8) | (flags.getValue() & 0xFF);
            case BC: return ((b & 0xFF) << 8) | (c & 0xFF);
            case DE: return ((d & 0xFF) << 8) | (e & 0xFF);
            case HL: return ((h & 0xFF) << 8) | (l & 0xFF);
            case SP: return sp.getValue();
            case PC: return pc.getValue();
            default: throw new InvalidRegisterException(register.toString());
        }
    }

    public void write16(Register16 register, int value) {
        value &= 0xFFFF; // 16-bit !!!!!!!!!!!!!!!
        switch (register) {
            case AF:
                a = (byte) ((value >> 8) & 0xFF);
                flags.setValue((byte) (value & 0xFF));
                break;
            case BC:
                b = (byte) ((value >> 8) & 0xFF);
                c = (byte) (value & 0xFF);
                break;
            case DE:
                d = (byte) ((value >> 8) & 0xFF);
                e = (byte) (value & 0xFF);
                break;
            case HL:
                h = (byte) ((value >> 8) & 0xFF);
                l = (byte) (value & 0xFF);
                break;
            case SP:
                sp.setValue(value);
                break;
            case PC:
                pc.setValue(value);
                break;
            default:
                throw new InvalidRegisterException(register.toString());
        }
    }

    // dca
    public FlagRegister getFlags() {
        return flags;
    }

    public ProgramCounter getProgramCounter() {
        return pc;
    }

    public StackPointer getStackPointer() {
        return sp;
    }

    // help
    public byte getA() {
        return a;
    }

    public void setA(byte value) {
        a = value;
    }

}
