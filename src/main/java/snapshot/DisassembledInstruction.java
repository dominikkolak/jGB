package snapshot;

import java.util.List;

public record DisassembledInstruction(int address,
                                      int opcode,
                                      List<Integer> operands,
                                      String mnemonic,
                                      int length
) {}
