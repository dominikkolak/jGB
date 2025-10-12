package cpu.decoder;

import cpu.exceptions.InvalidOpcodeException;
import cpu.instructions.Instruction;
import cpu.instructions.enums.Register8;
import cpu.instructions.types.LoadInstruction;
import cpu.instructions.types.MiscInstruction;
import cpu.instructions.types.ArithmeticInstruction;
import cpu.instructions.types.LogicInstruction;
import cpu.instructions.types.JumpInstruction;
import cpu.interfaces.BUS;
import cpu.interfaces.InstructionDecoder;

///
/// THIS DONT WORK !!!!!!!!!!!!!!!!!!!!!
///

public class OpcodeDecoder implements InstructionDecoder {

    @Override
    public Instruction decode(int pc, BUS bus) {
        int opcode = bus.readByte(pc) & 0xFF;

        try {
            return decodeStandardOpcode(opcode, pc, bus);
        } catch (Exception e) {
            throw new InvalidOpcodeException(opcode, pc);
        }
    }

    private Instruction decodeStandardOpcode(int opcode, int pc, BUS bus) {
        switch (opcode) {
            // NOP
            case 0x00:
                return MiscInstruction.nop();

            // HALT
            case 0x76:
                return MiscInstruction.halt();

            // LD r, n (8-bit immediate)
            case 0x3E: return new LoadInstruction(Register8.A, (byte) bus.readByte(pc + 1));
            case 0x06: return new LoadInstruction(Register8.B, (byte) bus.readByte(pc + 1));
            case 0x0E: return new LoadInstruction(Register8.C, (byte) bus.readByte(pc + 1));
            case 0x16: return new LoadInstruction(Register8.D, (byte) bus.readByte(pc + 1));
            case 0x1E: return new LoadInstruction(Register8.E, (byte) bus.readByte(pc + 1));
            case 0x26: return new LoadInstruction(Register8.H, (byte) bus.readByte(pc + 1));
            case 0x2E: return new LoadInstruction(Register8.L, (byte) bus.readByte(pc + 1));

            // LD r, r'
            case 0x7F: return new LoadInstruction(Register8.A, Register8.A);
            case 0x78: return new LoadInstruction(Register8.A, Register8.B);
            case 0x79: return new LoadInstruction(Register8.A, Register8.C);
            case 0x7A: return new LoadInstruction(Register8.A, Register8.D);
            case 0x7B: return new LoadInstruction(Register8.A, Register8.E);
            case 0x7C: return new LoadInstruction(Register8.A, Register8.H);
            case 0x7D: return new LoadInstruction(Register8.A, Register8.L);

            // r to A
            case 0x47: return new LoadInstruction(Register8.B, Register8.A);
            case 0x4F: return new LoadInstruction(Register8.C, Register8.A);
            case 0x57: return new LoadInstruction(Register8.D, Register8.A);
            case 0x5F: return new LoadInstruction(Register8.E, Register8.A);
            case 0x67: return new LoadInstruction(Register8.H, Register8.A);
            case 0x6F: return new LoadInstruction(Register8.L, Register8.A);

            // ADD A, r
            case 0x87: return ArithmeticInstruction.add(Register8.A);
            case 0x80: return ArithmeticInstruction.add(Register8.B);
            case 0x81: return ArithmeticInstruction.add(Register8.C);
            case 0x82: return ArithmeticInstruction.add(Register8.D);
            case 0x83: return ArithmeticInstruction.add(Register8.E);
            case 0x84: return ArithmeticInstruction.add(Register8.H);
            case 0x85: return ArithmeticInstruction.add(Register8.L);

            // ADC A, r
            case 0x8F: return ArithmeticInstruction.adc(Register8.A);
            case 0x88: return ArithmeticInstruction.adc(Register8.B);
            case 0x89: return ArithmeticInstruction.adc(Register8.C);
            case 0x8A: return ArithmeticInstruction.adc(Register8.D);
            case 0x8B: return ArithmeticInstruction.adc(Register8.E);
            case 0x8C: return ArithmeticInstruction.adc(Register8.H);
            case 0x8D: return ArithmeticInstruction.adc(Register8.L);

            // SUB r
            case 0x97: return ArithmeticInstruction.sub(Register8.A);
            case 0x90: return ArithmeticInstruction.sub(Register8.B);
            case 0x91: return ArithmeticInstruction.sub(Register8.C);
            case 0x92: return ArithmeticInstruction.sub(Register8.D);
            case 0x93: return ArithmeticInstruction.sub(Register8.E);
            case 0x94: return ArithmeticInstruction.sub(Register8.H);
            case 0x95: return ArithmeticInstruction.sub(Register8.L);

            // AND r
            case 0xA7: return LogicInstruction.and(Register8.A);
            case 0xA0: return LogicInstruction.and(Register8.B);
            case 0xA1: return LogicInstruction.and(Register8.C);
            case 0xA2: return LogicInstruction.and(Register8.D);
            case 0xA3: return LogicInstruction.and(Register8.E);
            case 0xA4: return LogicInstruction.and(Register8.H);
            case 0xA5: return LogicInstruction.and(Register8.L);

            // OR r
            case 0xB7: return LogicInstruction.or(Register8.A);
            case 0xB0: return LogicInstruction.or(Register8.B);
            case 0xB1: return LogicInstruction.or(Register8.C);
            case 0xB2: return LogicInstruction.or(Register8.D);
            case 0xB3: return LogicInstruction.or(Register8.E);
            case 0xB4: return LogicInstruction.or(Register8.H);
            case 0xB5: return LogicInstruction.or(Register8.L);

            // XOR r
            case 0xAF: return LogicInstruction.xor(Register8.A);
            case 0xA8: return LogicInstruction.xor(Register8.B);
            case 0xA9: return LogicInstruction.xor(Register8.C);
            case 0xAA: return LogicInstruction.xor(Register8.D);
            case 0xAB: return LogicInstruction.xor(Register8.E);
            case 0xAC: return LogicInstruction.xor(Register8.H);
            case 0xAD: return LogicInstruction.xor(Register8.L);

            // CP r
            case 0xBF: return LogicInstruction.cp(Register8.A);
            case 0xB8: return LogicInstruction.cp(Register8.B);
            case 0xB9: return LogicInstruction.cp(Register8.C);
            case 0xBA: return LogicInstruction.cp(Register8.D);
            case 0xBB: return LogicInstruction.cp(Register8.E);
            case 0xBC: return LogicInstruction.cp(Register8.H);
            case 0xBD: return LogicInstruction.cp(Register8.L);

            // INC r
            case 0x3C: return ArithmeticInstruction.inc(Register8.A);
            case 0x04: return ArithmeticInstruction.inc(Register8.B);
            case 0x0C: return ArithmeticInstruction.inc(Register8.C);
            case 0x14: return ArithmeticInstruction.inc(Register8.D);
            case 0x1C: return ArithmeticInstruction.inc(Register8.E);
            case 0x24: return ArithmeticInstruction.inc(Register8.H);
            case 0x2C: return ArithmeticInstruction.inc(Register8.L);

            // DEC r
            case 0x3D: return ArithmeticInstruction.dec(Register8.A);
            case 0x05: return ArithmeticInstruction.dec(Register8.B);
            case 0x0D: return ArithmeticInstruction.dec(Register8.C);
            case 0x15: return ArithmeticInstruction.dec(Register8.D);
            case 0x1D: return ArithmeticInstruction.dec(Register8.E);
            case 0x25: return ArithmeticInstruction.dec(Register8.H);
            case 0x2D: return ArithmeticInstruction.dec(Register8.L);

            // JP nn
            case 0xC3:
                int addr = bus.readWord(pc + 1);
                return new JumpInstruction(addr);

            // JP cc, nn
            case 0xC2: // JP NZ, nn
                return new JumpInstruction(bus.readWord(pc + 1), Condition.NZ);
            case 0xCA: // JP Z, nn
                return new JumpInstruction(bus.readWord(pc + 1), Condition.Z);
            case 0xD2: // JP NC, nn
                return new JumpInstruction(bus.readWord(pc + 1), Condition.NC);
            case 0xDA: // JP C, nn
                return new JumpInstruction(bus.readWord(pc + 1), Condition.C);

            // DI / EI
            case 0xF3: return MiscInstruction.di();
            case 0xFB: return MiscInstruction.ei();

            default:
                throw new InvalidOpcodeException(opcode, pc);
        }
    }

}
