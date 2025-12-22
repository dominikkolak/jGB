package cpu.instructions.types;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.instructions.Instruction;
import cpu.interfaces.BUS;

/// Not implemented

public class LogicInstruction implements Instruction {

    public LogicInstruction() {
        // Placeholder
    }

    @Override
    public int execute(RegisterFile registers, ALU alu, BUS bus) {
        // TODO: Logic instruction
        return 1;
    }

    @Override
    public String getMnemonic() {
        return "LOGIC (NOT IMPLEMENTED!!!)";
    }

    @Override
    public int getSize() {
        return 1;
    }

}
