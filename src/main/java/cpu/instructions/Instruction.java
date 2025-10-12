package cpu.instructions;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.interfaces.BUS;

public interface Instruction {
    int execute(RegisterFile registers, ALU alu, BUS bus);

    String getMnemonic();

    int getSize();
}
