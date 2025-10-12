package cpu.interfaces;

import cpu.instructions.Instruction;

public interface InstructionDecoder {

    Instruction decode(int pc, BUS bus);

}
