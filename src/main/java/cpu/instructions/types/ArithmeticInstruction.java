package cpu.instructions.types;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.instructions.Instruction;
import cpu.instructions.enums.Register8;
import cpu.interfaces.BUS;

public class ArithmeticInstruction implements Instruction {

    private enum ArithType {
        ADD,
        ADC,
        SUB,
        SBC,
        INC,
        DEC
    }

    private final ArithType type;
    private final Register8 register;

    public ArithmeticInstruction(ArithType type, Register8 register) {
        this.type = type;
        this.register = register;
    }

    public static ArithmeticInstruction add(Register8 register) {
        return new ArithmeticInstruction(ArithType.ADD, register);
    }

    public static ArithmeticInstruction adc(Register8 register) {
        return new ArithmeticInstruction(ArithType.ADC, register);
    }

    public static ArithmeticInstruction sub(Register8 register) {
        return new ArithmeticInstruction(ArithType.SUB, register);
    }

    public static ArithmeticInstruction sbc(Register8 register) {
        return new ArithmeticInstruction(ArithType.SBC, register);
    }

    public static ArithmeticInstruction inc(Register8 register) {
        return new ArithmeticInstruction(ArithType.INC, register);
    }

    public static ArithmeticInstruction dec(Register8 register) {
        return new ArithmeticInstruction(ArithType.DEC, register);
    }

    @Override
    public int execute(RegisterFile registers, ALU alu, BUS bus) {
        byte a = registers.getA();
        byte value = registers.read8(register);
        byte result;

        switch (type) {
            case ADD:
                result = alu.add(a, value, false);
                registers.setA(result);
                return 1;

            case ADC:
                result = alu.add(a, value, true);
                registers.setA(result);
                return 1;

            case SUB:
                result = alu.sub(a, value, false);
                registers.setA(result);
                return 1;

            case SBC:
                result = alu.sub(a, value, true);
                registers.setA(result);
                return 1;

            case INC:
                result = alu.increment(value);
                registers.write8(register, result);
                return 1;

            case DEC:
                result = alu.decrement(value);
                registers.write8(register, result);
                return 1;

            default:
                return 1;
        }
    }

    @Override
    public String getMnemonic() {
        switch (type) {
            case ADD: return "ADD A, " + register;
            case ADC: return "ADC A, " + register;
            case SUB: return "SUB " + register;
            case SBC: return "SBC A, " + register;
            case INC: return "INC " + register;
            case DEC: return "DEC " + register;
            default: return "ARITH";
        }
    }

    @Override
    public int getSize() {
        return 1;
    }

}
