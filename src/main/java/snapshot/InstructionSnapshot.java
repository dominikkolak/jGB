package snapshot;

import java.util.List;

public record InstructionSnapshot(DisassembledInstruction current,
                                  List<DisassembledInstruction> lookahead
) {}




