package cpu.instruction.tables;

import cpu.instruction.Instruction;

import cpu.instruction.definitions.*;
import cpu.register.enums.R8;

public class SecondaryOpcodeTable {

    private static final Instruction[] TABLE = new Instruction[256];

    // B, C, D, E, H, L, (HL), A
    private static final R8[] R8_TABLE = { R8.B, R8.C, R8.D, R8.E, R8.H, R8.L, null, R8.A };

    static {
        for (int op = 0x00; op <= 0x3F; op++) {
            int operation = (op >> 3) & 0x07;
            int regIdx = op & 0x07;
            TABLE[op] = rotateShift(operation, regIdx);
        }

        for (int op = 0x40; op <= 0x7F; op++) {
            int bit = (op >> 3) & 0x07;
            int regIdx = op & 0x07;
            R8 r = R8_TABLE[regIdx];
            TABLE[op] = r != null ? BitInstructions.BIT_N_R8(bit, r)
                    : BitInstructions.BIT_N_HL(bit);
        }

        for (int op = 0x80; op <= 0xBF; op++) {
            int bit = (op >> 3) & 0x07;
            int regIdx = op & 0x07;
            R8 r = R8_TABLE[regIdx];
            TABLE[op] = r != null ? BitInstructions.RES_N_R8(bit, r)
                    : BitInstructions.RES_N_HL(bit);
        }

        for (int op = 0xC0; op <= 0xFF; op++) {
            int bit = (op >> 3) & 0x07;
            int regIdx = op & 0x07;
            R8 r = R8_TABLE[regIdx];
            TABLE[op] = r != null ? BitInstructions.SET_N_R8(bit, r)
                    : BitInstructions.SET_N_HL(bit);
        }
    }

    public static Instruction get(int opcode) {
        return TABLE[opcode & 0xFF];
    }

    private static Instruction rotateShift(int op, int regIdx) {
        R8 r = R8_TABLE[regIdx];
        return switch (op) {
            case 0 -> r != null ? RotateShiftInstructions.RLC_R8(r)  : RotateShiftInstructions.RLC_HL();
            case 1 -> r != null ? RotateShiftInstructions.RRC_R8(r)  : RotateShiftInstructions.RRC_HL();
            case 2 -> r != null ? RotateShiftInstructions.RL_R8(r)   : RotateShiftInstructions.RL_HL();
            case 3 -> r != null ? RotateShiftInstructions.RR_R8(r)   : RotateShiftInstructions.RR_HL();
            case 4 -> r != null ? RotateShiftInstructions.SLA_R8(r)  : RotateShiftInstructions.SLA_HL();
            case 5 -> r != null ? RotateShiftInstructions.SRA_R8(r)  : RotateShiftInstructions.SRA_HL();
            case 6 -> r != null ? RotateShiftInstructions.SWAP_R8(r) : RotateShiftInstructions.SWAP_HL();
            case 7 -> r != null ? RotateShiftInstructions.SRL_R8(r)  : RotateShiftInstructions.SRL_HL();
            default -> throw new IllegalStateException();
        };
    }

    public static boolean isValid(int opcode) {
        return TABLE[opcode & 0xFF] != null;
    }

}
