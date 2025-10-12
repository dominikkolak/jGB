package cpu.cpu.instruction.tables;

import cpu.instruction.Instruction;
import cpu.instruction.definitions.*;
import cpu.register.enums.CONDITION;
import cpu.register.enums.R8;

import static cpu.register.enums.CONDITION.*;
import static cpu.register.enums.R16.*;
import static cpu.register.enums.R8.*;

public class PrimaryOpcodeTable {

    private static final Instruction[] TABLE = new Instruction[256];

    // B, C, D, E, H, L, (HL), A
    private static final R8[] R8_TABLE = { R8.B, R8.C, R8.D, R8.E, R8.H, R8.L, null, R8.A };

    static {
        TABLE[0x00] = SpecialInstructions.NOP();
        TABLE[0x01] = LoadInstructions.LD_R16_NN(BC);
        TABLE[0x02] = LoadInstructions.LD_BC_A();
        TABLE[0x03] = ArithmeticInstructions.INC_R16(BC);
        TABLE[0x04] = ArithmeticInstructions.INC_R8(B);
        TABLE[0x05] = ArithmeticInstructions.DEC_R8(B);
        TABLE[0x06] = LoadInstructions.LD_R8_N(B);
        TABLE[0x07] = RotateShiftInstructions.RLCA();
        TABLE[0x08] = LoadInstructions.LD_NN_SP();
        TABLE[0x09] = ArithmeticInstructions.ADD_HL_R16(BC);
        TABLE[0x0A] = LoadInstructions.LD_A_BC();
        TABLE[0x0B] = ArithmeticInstructions.DEC_R16(BC);
        TABLE[0x0C] = ArithmeticInstructions.INC_R8(R8.C);
        TABLE[0x0D] = ArithmeticInstructions.DEC_R8(R8.C);
        TABLE[0x0E] = LoadInstructions.LD_R8_N(R8.C);
        TABLE[0x0F] = RotateShiftInstructions.RRCA();

        TABLE[0x10] = SpecialInstructions.STOP();
        TABLE[0x11] = LoadInstructions.LD_R16_NN(DE);
        TABLE[0x12] = LoadInstructions.LD_DE_A();
        TABLE[0x13] = ArithmeticInstructions.INC_R16(DE);
        TABLE[0x14] = ArithmeticInstructions.INC_R8(D);
        TABLE[0x15] = ArithmeticInstructions.DEC_R8(D);
        TABLE[0x16] = LoadInstructions.LD_R8_N(D);
        TABLE[0x17] = RotateShiftInstructions.RLA();
        TABLE[0x18] = JumpInstructions.JR_E();
        TABLE[0x19] = ArithmeticInstructions.ADD_HL_R16(DE);
        TABLE[0x1A] = LoadInstructions.LD_A_DE();
        TABLE[0x1B] = ArithmeticInstructions.DEC_R16(DE);
        TABLE[0x1C] = ArithmeticInstructions.INC_R8(E);
        TABLE[0x1D] = ArithmeticInstructions.DEC_R8(E);
        TABLE[0x1E] = LoadInstructions.LD_R8_N(E);
        TABLE[0x1F] = RotateShiftInstructions.RRA();

        TABLE[0x20] = JumpInstructions.JR_CC_E(NZ);
        TABLE[0x21] = LoadInstructions.LD_R16_NN(HL);
        TABLE[0x22] = LoadInstructions.LD_HLI_A();
        TABLE[0x23] = ArithmeticInstructions.INC_R16(HL);
        TABLE[0x24] = ArithmeticInstructions.INC_R8(H);
        TABLE[0x25] = ArithmeticInstructions.DEC_R8(H);
        TABLE[0x26] = LoadInstructions.LD_R8_N(H);
        TABLE[0x27] = ArithmeticInstructions.DAA();
        TABLE[0x28] = JumpInstructions.JR_CC_E(Z);
        TABLE[0x29] = ArithmeticInstructions.ADD_HL_R16(HL);
        TABLE[0x2A] = LoadInstructions.LD_A_HLI();
        TABLE[0x2B] = ArithmeticInstructions.DEC_R16(HL);
        TABLE[0x2C] = ArithmeticInstructions.INC_R8(L);
        TABLE[0x2D] = ArithmeticInstructions.DEC_R8(L);
        TABLE[0x2E] = LoadInstructions.LD_R8_N(L);
        TABLE[0x2F] = ArithmeticInstructions.CPL();

        TABLE[0x30] = JumpInstructions.JR_CC_E(NC);
        TABLE[0x31] = LoadInstructions.LD_R16_NN(SP);
        TABLE[0x32] = LoadInstructions.LD_HLD_A();
        TABLE[0x33] = ArithmeticInstructions.INC_R16(SP);
        TABLE[0x34] = ArithmeticInstructions.INC_HL();
        TABLE[0x35] = ArithmeticInstructions.DEC_HL();
        TABLE[0x36] = LoadInstructions.LD_HL_N();
        TABLE[0x37] = ArithmeticInstructions.SCF();
        TABLE[0x38] = JumpInstructions.JR_CC_E(CONDITION.C);
        TABLE[0x39] = ArithmeticInstructions.ADD_HL_R16(SP);
        TABLE[0x3A] = LoadInstructions.LD_A_HLD();
        TABLE[0x3B] = ArithmeticInstructions.DEC_R16(SP);
        TABLE[0x3C] = ArithmeticInstructions.INC_R8(A);
        TABLE[0x3D] = ArithmeticInstructions.DEC_R8(A);
        TABLE[0x3E] = LoadInstructions.LD_R8_N(A);
        TABLE[0x3F] = ArithmeticInstructions.CCF();

        for (int op = 0x40; op <= 0x7F; op++) {
            if (op == 0x76) {
                TABLE[op] = SpecialInstructions.HALT();
            } else {
                int dst = (op >> 3) & 0x07;
                int src = op & 0x07;
                TABLE[op] = ldBlock(dst, src);
            }
        }

        for (int op = 0x80; op <= 0xBF; op++) {
            int alu = (op >> 3) & 0x07;
            int src = op & 0x07;
            TABLE[op] = aluBlock(alu, src);
        }

        TABLE[0xC0] = CallInstructions.RET_CC(NZ);
        TABLE[0xC1] = StackInstructions.POP_R16(BC);
        TABLE[0xC2] = JumpInstructions.JP_CC_NN(NZ);
        TABLE[0xC3] = JumpInstructions.JP_NN();
        TABLE[0xC4] = CallInstructions.CALL_CC_NN(NZ);
        TABLE[0xC5] = StackInstructions.PUSH_R16(BC);
        TABLE[0xC6] = ArithmeticInstructions.ADD_A_N();
        TABLE[0xC7] = CallInstructions.RST(0x00);
        TABLE[0xC8] = CallInstructions.RET_CC(Z);
        TABLE[0xC9] = CallInstructions.RET();
        TABLE[0xCA] = JumpInstructions.JP_CC_NN(Z);

        // 0xCB = prefix

        TABLE[0xCC] = CallInstructions.CALL_CC_NN(Z);
        TABLE[0xCD] = CallInstructions.CALL_NN();
        TABLE[0xCE] = ArithmeticInstructions.ADC_A_N();
        TABLE[0xCF] = CallInstructions.RST(0x08);

        TABLE[0xD0] = CallInstructions.RET_CC(NC);
        TABLE[0xD1] = StackInstructions.POP_R16(DE);
        TABLE[0xD2] = JumpInstructions.JP_CC_NN(NC);

        // 0xD3 invalid

        TABLE[0xD4] = CallInstructions.CALL_CC_NN(NC);
        TABLE[0xD5] = StackInstructions.PUSH_R16(DE);
        TABLE[0xD6] = ArithmeticInstructions.SUB_N();
        TABLE[0xD7] = CallInstructions.RST(0x10);
        TABLE[0xD8] = CallInstructions.RET_CC(CONDITION.C);
        TABLE[0xD9] = CallInstructions.RETI();
        TABLE[0xDA] = JumpInstructions.JP_CC_NN(CONDITION.C);

        // 0xDB invalid

        TABLE[0xDC] = CallInstructions.CALL_CC_NN(CONDITION.C);

        // 0xDD invalid

        TABLE[0xDE] = ArithmeticInstructions.SBC_A_N();
        TABLE[0xDF] = CallInstructions.RST(0x18);

        TABLE[0xE0] = LoadInstructions.LDH_N_A();
        TABLE[0xE1] = StackInstructions.POP_R16(HL);
        TABLE[0xE2] = LoadInstructions.LDH_C_A();

        // 0xE3, 0xE4 invalid

        TABLE[0xE5] = StackInstructions.PUSH_R16(HL);
        TABLE[0xE6] = LogicInstructions.AND_N();
        TABLE[0xE7] = CallInstructions.RST(0x20);
        TABLE[0xE8] = ArithmeticInstructions.ADD_SP_E();
        TABLE[0xE9] = JumpInstructions.JP_HL();
        TABLE[0xEA] = LoadInstructions.LD_NN_A();

        // 0xEB, 0xEC, 0xED invalid

        TABLE[0xEE] = LogicInstructions.XOR_N();
        TABLE[0xEF] = CallInstructions.RST(0x28);

        // 0xF0-0xFF
        TABLE[0xF0] = LoadInstructions.LDH_A_N();
        TABLE[0xF1] = StackInstructions.POP_R16(AF);
        TABLE[0xF2] = LoadInstructions.LDH_A_C();
        TABLE[0xF3] = SpecialInstructions.DI();

        // 0xF4 invalid

        TABLE[0xF5] = StackInstructions.PUSH_R16(AF);
        TABLE[0xF6] = LogicInstructions.OR_N();
        TABLE[0xF7] = CallInstructions.RST(0x30);
        TABLE[0xF8] = LoadInstructions.LD_HL_SP_E();
        TABLE[0xF9] = LoadInstructions.LD_SP_HL();
        TABLE[0xFA] = LoadInstructions.LD_A_NN();
        TABLE[0xFB] = SpecialInstructions.EI();

        // 0xFC, 0xFD invalid

        TABLE[0xFE] = ArithmeticInstructions.CP_N();
        TABLE[0xFF] = CallInstructions.RST(0x38);
    }

    public static Instruction get(int opcode) {
        return TABLE[opcode & 0xFF];
    }

    public static boolean isValid(int opcode) {
        return TABLE[opcode & 0xFF] != null;
    }

    public static boolean isCBPrefix(int opcode) {
        return (opcode & 0xFF) == 0xCB;
    }

    private static Instruction ldBlock(int dst, int src) {
        R8 d = R8_TABLE[dst];
        R8 s = R8_TABLE[src];
        if (d == null) return LoadInstructions.LD_HL_R8(s);
        if (s == null) return LoadInstructions.LD_R8_HL(d);
        return LoadInstructions.LD_R8_R8(d, s);
    }

    private static Instruction aluBlock(int alu, int src) {
        R8 r = R8_TABLE[src];
        return switch (alu) {
            case 0 -> r != null ? ArithmeticInstructions.ADD_A_R8(r) : ArithmeticInstructions.ADD_A_HL();
            case 1 -> r != null ? ArithmeticInstructions.ADC_A_R8(r) : ArithmeticInstructions.ADC_A_HL();
            case 2 -> r != null ? ArithmeticInstructions.SUB_R8(r)   : ArithmeticInstructions.SUB_HL();
            case 3 -> r != null ? ArithmeticInstructions.SBC_A_R8(r) : ArithmeticInstructions.SBC_A_HL();
            case 4 -> r != null ? LogicInstructions.AND_R8(r)        : LogicInstructions.AND_HL();
            case 5 -> r != null ? LogicInstructions.XOR_R8(r)        : LogicInstructions.XOR_HL();
            case 6 -> r != null ? LogicInstructions.OR_R8(r)         : LogicInstructions.OR_HL();
            case 7 -> r != null ? ArithmeticInstructions.CP_R8(r)    : ArithmeticInstructions.CP_HL();
            default -> throw new IllegalStateException();
        };
    }

}
