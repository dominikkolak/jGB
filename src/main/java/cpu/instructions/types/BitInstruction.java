package cpu.instructions.types;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.instructions.Instruction;
import cpu.interfaces.BUS;

/// Not implemented
/// CB !!!

public class BitInstruction implements Instruction {

    public BitInstruction() {
        // Placeholder
    }

    @Override
    public int execute(RegisterFile registers, ALU alu, BUS bus) {
        // TODO: bit instructions
        return 2;
    }

    @Override
    public String getMnemonic() {
        return "BIT (NOT IMPLEMENTED)";
    }

    @Override
    public int getSize() {
        return 2; // CB prefix + opcode
    }

}
