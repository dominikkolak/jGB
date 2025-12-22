package cpu.instructions.types;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.instructions.Instruction;
import cpu.instructions.enums.Condition;
import cpu.interfaces.BUS;

public class JumpInstruction implements Instruction {

    private final int address;
    private final Condition condition;

    public JumpInstruction(int address, Condition condition) {
        this.address = address & 0xFFFF;
        this.condition = condition;
    }

    public JumpInstruction(int address) {
        this(address, Condition.NONE);
    }

    @Override
    public int execute(RegisterFile registers, ALU alu, BUS bus) {
        boolean shouldJump = checkCondition(registers, condition);

        if (!shouldJump) {
            return 3; // 12 cycles, if not taken!!!
        }

        registers.getProgramCounter().setValue(address);
        return 4; // 16 cycles
    }

    private boolean checkCondition(RegisterFile registers, Condition condition) {
        switch (condition) {
            case NONE: return true;
            case Z: return registers.getFlags().getZero();
            case NZ: return !registers.getFlags().getZero();
            case C: return registers.getFlags().getCarry();
            case NC: return !registers.getFlags().getCarry();
            default: return false;
        }
    }

    @Override
    public String getMnemonic() {
        if (condition == Condition.NONE) {
            return String.format("JP 0x%04X", address);
        }
        return String.format("JP %s, 0x%04X", condition, address);
    }

    @Override
    public int getSize() {
        return 3;
    }

}
