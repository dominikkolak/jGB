package cpu.instruction.definitions;

import cpu.instruction.Instruction;
import cpu.register.enums.R8;

import static cpu.instruction.CycleState.*;
import static cpu.register.enums.R16.*;
import static cpu.register.enums.R8.*;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class RotateShiftInstructions {

    /*
    RLCA: Rotate left circular (accumulator)
    Rotates the 8-bit A register value left in a circular manner (carry flag is updated but not used).
    Every bit is shifted to the left (e.g. bit 1 value is copied from bit 0). Bit 7 is copied both to bit
    0 and the carry flag. Note that unlike the related RLC r instruction, RLCA always sets the zero
    flag to 0 without looking at the resulting value of the calculation.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 b7

    Opcode 0b00000111/0x07
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = 0, N = 0, H = 0, C = X
     */
    public static Instruction RLCA() {
        return Instruction.create(1, "RLCA", ctx -> {
            var res = ctx.rlc(ctx.readReg8(A));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(false, false, false, res.carry());
            return DONE;
        });
    }

    /*
    RRCA: Rotate right circular (accumulator)
    Rotates the 8-bit A register value right in a circular manner (carry flag is updated but not used).
    Every bit is shifted to the right (e.g. bit 1 value is copied to bit 0). Bit 0 is copied both to bit 7
    and the carry flag. Note that unlike the related RRC r instruction, RRCA always sets the zero
    flag to 0 without looking at the resulting value of the calculation.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After b0 b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0b00001111/0x0F
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = 0, N = 0, H = 0, C = X
     */
    public static Instruction RRCA() {
        return Instruction.create(1, "RRCA", ctx -> {
            var res = ctx.rrc(ctx.readReg8(A));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(false, false, false, res.carry());
            return DONE;
        });
    }

    /*
    RLA: Rotate left (accumulator)
    Rotates the 8-bit A register value left through the carry flag.
    Every bit is shifted to the left (e.g. bit 1 value is copied from bit 0). The carry flag is copied to bit
    0, and bit 7 is copied to the carry flag. Note that unlike the related RL r instruction, RLA always
    sets the zero flag to 0 without looking at the resulting value of the calculation.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 C

    Opcode 0b00010111/0x17
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = 0, N = 0, H = 0, C = X
     */
    public static Instruction RLA() {
        return Instruction.create(1, "RLA", ctx -> {
            var res = ctx.rl(ctx.readReg8(A));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(false, false, false, res.carry());
            return DONE;
        });
    }

    /*
    RRA: Rotate right (accumulator)
    Rotates the 8-bit A register value right through the carry flag.
    Every bit is shifted to the right (e.g. bit 1 value is copied to bit 0). The carry flag is copied to bit
    7, and bit 0 is copied to the carry flag. Note that unlike the related RR r instruction, RRA always
    sets the zero flag to 0 without looking at the resulting value of the calculation.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After C b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0b00011111/0x1F
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = 0, N = 0, H = 0, C = X
     */
    public static Instruction RRA() {
        return Instruction.create(1, "RRA", ctx -> {
            var res = ctx.rr(ctx.readReg8(A));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(false, false, false, res.carry());
            return DONE;
        });
    }

    /*
    RLC r: Rotate left circular (register)
    Rotates the 8-bit register r value left in a circular manner (carry flag is updated but not used).
    Every bit is shifted to the left (e.g. bit 1 value is copied from bit 0). Bit 7 is copied both to bit 0
    and the carry flag.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 b7

    Opcode 0b00000xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction RLC_R8(R8 r) {
        return Instruction.create(2, "RLC " + r, ctx -> {
            var res = ctx.rlc(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    RLC (HL): Rotate left circular (indirect HL)
    Rotates, the 8-bit data at the absolute address specified by the 16-bit register HL, left in a
    circular manner (carry flag is updated but not used).
    Every bit is shifted to the left (e.g. bit 1 value is copied from bit 0). Bit 7 is copied both to bit 0
    and the carry flag.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 b7

    Opcode 0x06
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C =X
     */
    public static Instruction RLC_HL() {
        return Instruction.create(2, "RLC (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.rlc(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    RRC r: Rotate right circular (register)
    Rotates the 8-bit register r value right in a circular manner (carry flag is updated but not used).
    Every bit is shifted to the right (e.g. bit 1 value is copied to bit 0). Bit 0 is copied both to bit 7
    and the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After b0 b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0b00001xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction RRC_R8(R8 r) {
        return Instruction.create(2, "RRC " + r, ctx -> {
            var res = ctx.rrc(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    RRC (HL): Rotate right circular (indirect HL)
    Rotates, the 8-bit data at the absolute address specified by the 16-bit register HL, right in a
    circular manner (carry flag is updated but not used).
    Every bit is shifted to the right (e.g. bit 1 value is copied to bit 0). Bit 0 is copied both to bit 7
    and the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After b0 b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0x0E
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction RRC_HL() {
        return Instruction.create(2, "RRC (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.rrc(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    RL r: Rotate left (register)
    Rotates the 8-bit register r value left through the carry flag.
    Every bit is shifted to the left (e.g. bit 1 value is copied from bit 0). The carry flag is copied to bit
    0, and bit 7 is copied to the carry flag.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 C

    Opcode 0b00010xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction RL_R8(R8 r) {
        return Instruction.create(2, "RL " + r, ctx -> {
            var res = ctx.rl(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    RL (HL): Rotate left (indirect HL)
    Rotates, the 8-bit data at the absolute address specified by the 16-bit register HL, left through
    the carry flag.
    Every bit is shifted to the left (e.g. bit 1 value is copied from bit 0). The carry flag is copied to bit
    0, and bit 7 is copied to the carry flag.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 C

    Opcode 0x16
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction RL_HL() {
        return Instruction.create(2, "RL (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.rl(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    RR r: Rotate right (register)
    Rotates the 8-bit register r value right through the carry flag.
    Every bit is shifted to the right (e.g. bit 1 value is copied to bit 0). The carry flag is copied to bit
    7, and bit 0 is copied to the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After C b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0b00011xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction RR_R8(R8 r) {
        return Instruction.create(2, "RR " + r, ctx -> {
            var res = ctx.rr(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    RR (HL): Rotate right (indirect HL)
    Rotates, the 8-bit data at the absolute address specified by the 16-bit register HL, right through
    the carry flag.
    Every bit is shifted to the right (e.g. bit 1 value is copied to bit 0). The carry flag is copied to bit
    7, and bit 0 is copied to the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After C b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0x1E
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction RR_HL() {
        return Instruction.create(2, "RR (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.rr(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    SLA r: Shift left arithmetic (register)
    Shifts the 8-bit register r value left by one bit using an arithmetic shift.
    Bit 7 is shifted to the carry flag, and bit 0 is set to a fixed value of 0.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 0

    Opcode 0b00100xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction SLA_R8(R8 r) {
        return Instruction.create(2, "SLA " + r, ctx -> {
            var res = ctx.sla(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SLA (HL): Shift left arithmetic (indirect HL)
    Shifts, the 8-bit value at the address specified by the HL register, left by one bit using an
    arithmetic shift.
    Bit 7 is shifted to the carry flag, and bit 0 is set to a fixed value of 0.

    Before C b7 b6 b5 b4 b3 b2 b1 b0
    After b7 b6 b5 b4 b3 b2 b1 b0 0

    Opcode 0x26 Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction SLA_HL() {
        return Instruction.create(2, "SLA (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.sla(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    SRA r: Shift right arithmetic (register)
    Shifts the 8-bit register r value right by one bit using an arithmetic shift.
    Bit 7 retains its value, and bit 0 is shifted to the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After b7 b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0b00101xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction SRA_R8(R8 r) {
        return Instruction.create(2, "SRA " + r, ctx -> {
            var res = ctx.sra(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SRA (HL): Shift right arithmetic (indirect HL)
    Shifts, the 8-bit value at the address specified by the HL register, right by one bit using an
    arithmetic shift.
    Bit 7 retains its value, and bit 0 is shifted to the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After b7 b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0x2E
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction SRA_HL() {
        return Instruction.create(2, "SRA (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.sra(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    SRL r: Shift right logical (register)
    Shifts the 8-bit register r value right by one bit using a logical shift.
    Bit 7 is set to a fixed value of 0, and bit 0 is shifted to the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After 0 b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0b00111xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction SRL_R8(R8 r) {
        return Instruction.create(2, "SRL " + r, ctx -> {
            var res = ctx.srl(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SRL (HL): Shift right logical (indirect HL)
    Shifts, the 8-bit value at the address specified by the HL register, right by one bit using a logical
    shift.
    Bit 7 is set to a fixed value of 0, and bit 0 is shifted to the carry flag.

    Before b7 b6 b5 b4 b3 b2 b1 b0 C
    After 0 b7 b6 b5 b4 b3 b2 b1 b0

    Opcode 0x3E
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = X
     */
    public static Instruction SRL_HL() {
        return Instruction.create(2, "SRL (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.srl(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    SWAP r: Swap nibbles (register)
    Swaps the high and low 4-bit nibbles of the 8-bit register r.

    Before b7 b6 b5 b4 b3 b2 b1 b0
    After b3 b2 b1 b0 b7 b6 b5 b4

    Opcode 0b00110xxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = 0
     */
    public static Instruction SWAP_R8(R8 r) {
        return Instruction.create(2, "SWAP " + r, ctx -> {
            var res = ctx.swap(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SWAP (HL): Swap nibbles (indirect HL)
    Swaps the high and low 4-bit nibbles of the 8-bit data at the absolute address specified by the
    16-bit register HL.

    Before b7 b6 b5 b4 b3 b2 b1 b0
    After b3 b2 b1 b0 b7 b6 b5 b4

    Opcode 0x36
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 0, C = 0
     */
    public static Instruction SWAP_HL() {
        return Instruction.create(2, "SWAP (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.swap(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

}
