package cpu.interfaces;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.instructions.Instruction;

public interface InstructionExecutor {

    int execute(Instruction instruction, RegisterFile registers, ALU alu, BUS bus);

}
