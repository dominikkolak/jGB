package cpu.instruction.definitions;

import cpu.instruction.Instruction;
import cpu.register.enums.R8;

import static cpu.instruction.CycleState.DONE;
import static cpu.register.enums.R16.HL;
import static cpu.register.enums.R8.A;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class LogicInstructions {

    /*
    AND r: Bitwise AND (register)
    Performs a bitwise AND operation between the 8-bit A register and the 8-bit register r, and
    stores the result back into the A register.
    Opcode 0b10100xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = 1, C = 0
     */
    public static Instruction AND_R8(R8 r) {
        return Instruction.create(1, "AND " + r, ctx -> {
            var res = ctx.and(ctx.readReg8(A), ctx.readReg8(r));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    AND (HL): Bitwise AND (indirect HL)
    Performs a bitwise AND operation between the 8-bit A register and data from the absolute
    address specified by the 16-bit register HL, and stores the result back into the A register.
    Opcode 0b10100110/0xA6
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = 1, C = 0
     */
    public static Instruction AND_HL() {
        return Instruction.create(1, "AND (HL)", ctx -> {
            var res = ctx.and(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL)));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    AND n: Bitwise AND (immediate)
    Performs a bitwise AND operation between the 8-bit A register and immediate data n, and
    stores the result back into the A register.
    Opcode 0b11100110/0xE6
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = X, N = 0, H = 1, C = 0
     */
    public static Instruction AND_N() {
        return Instruction.create(2, "AND n", ctx -> {
            var res = ctx.and(ctx.readReg8(A), ctx.fetchByte());
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    OR r: Bitwise OR (register)
    Performs a bitwise OR operation between the 8-bit A register and the 8-bit register r, and stores
    the result back into the A register.
    Opcode 0b10110xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = 0, C = 0
     */
    public static Instruction OR_R8(R8 r) {
        return Instruction.create(1, "OR " + r, ctx -> {
            var res = ctx.or(ctx.readReg8(A), ctx.readReg8(r));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    OR (HL): Bitwise OR (indirect HL)
    Performs a bitwise OR operation between the 8-bit A register and data from the absolute
    address specified by the 16-bit register HL, and stores the result back into the A register.
    Opcode 0b10110110/0xB6
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = 0, C = 0
     */
    public static Instruction OR_HL() {
        return Instruction.create(1, "OR (HL)", ctx -> {
            var res = ctx.or(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL)));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    OR n: Bitwise OR (immediate)
    Performs a bitwise OR operation between the 8-bit A register and immediate data n, and stores
    the result back into the A register.
    Opcode 0b11110110/0xF6
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = X, N = 0, H = 0, C = 0
     */
    public static Instruction OR_N() {
        return Instruction.create(2, "OR n", ctx -> {
            var res = ctx.or(ctx.readReg8(A), ctx.fetchByte());
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    XOR r: Bitwise XOR (register)
    Performs a bitwise XOR operation between the 8-bit A register and the 8-bit register r, and
    stores the result back into the A register.
    Opcode 0b10101xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = 0, C = 0
     */
    public static Instruction XOR_R8(R8 r) {
        return Instruction.create(1, "XOR " + r, ctx -> {
            var res = ctx.xor(ctx.readReg8(A), ctx.readReg8(r));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    XOR (HL): Bitwise XOR (indirect HL)
    Performs a bitwise XOR operation between the 8-bit A register and data from the absolute
    address specified by the 16-bit register HL, and stores the result back into the A register.
    Opcode 0b10101110/0xAE
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = 0, C = 0
     */
    public static Instruction XOR_HL() {
        return Instruction.create(1, "XOR (HL)", ctx -> {
            var res = ctx.xor(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL)));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    XOR n: Bitwise XOR (immediate)
    Performs a bitwise XOR operation between the 8-bit A register and immediate data n, and
    stores the result back into the A register.
    Opcode 0b11101110/0xEE
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = , N = 0, H = 0, C = 0
     */
    public static Instruction XOR_N() {
        return Instruction.create(2, "XOR n", ctx -> {
            var res = ctx.xor(ctx.readReg8(A), ctx.fetchByte());
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

}
