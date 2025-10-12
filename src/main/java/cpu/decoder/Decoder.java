package cpu.decoder;

import cpu.instruction.Instruction;
import cpu.instruction.tables.PrimaryOpcodeTable;
import cpu.instruction.tables.SecondaryOpcodeTable;

public class Decoder {

    public static final int CB_PREFIX = 0xCB;

    public static Instruction decode(int opcode) {
        return PrimaryOpcodeTable.get(opcode);
    }

    public static Instruction decodeCB(int opcode) {
        return SecondaryOpcodeTable.get(opcode);
    }

    public static boolean isCBPrefix(int opcode) {
        return (opcode & 0xFF) == CB_PREFIX;
    }

    public static boolean isValid(int opcode) {
        return PrimaryOpcodeTable.isValid(opcode);
    }
    public static boolean isValidCB(int opcode) { return SecondaryOpcodeTable.isValid(opcode); }

}
