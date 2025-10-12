package cpu.cpu.instruction.definitions;


import cpu.instruction.Instruction;
import cpu.register.enums.R16;
import cpu.register.enums.R8;

import static cpu.instruction.CycleState.CONTINUE;
import static cpu.instruction.CycleState.DONE;
import static cpu.register.enums.R16.*;
import static cpu.register.enums.R8.A;
import static cpu.register.enums.R8.C;

// Credit for Instruction descriptions to:
// Joonas Javanainen (gekkio)
// Game Boy: Complete Technical Reference
// https://gekkio.fi

public class LoadInstructions {

    /*
    LD r, r’: Load register (register)
    Load to the 8-bit register r, data from the 8-bit register r'.
    Opcode 0b01xxxyyy/various
    Duration 1 machine cycle
    Length 1 byte:
    opcode Flags -
     */
    public static Instruction LD_R8_R8(R8 dst, R8 src) {
        return Instruction.create(1, "LD " + dst + " " + src, ctx -> {
            ctx.writeReg8(dst, ctx.readReg8(src));
            return DONE;
        });
    }

    /*
    LD r, n: Load register (immediate)
    Load to the 8-bit register r, the immediate data n.
    Opcode 0b00xxx110/various
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags -
     */
    public static Instruction LD_R8_N(R8 reg) {
        return Instruction.create(2, "LD " + reg + " n", ctx -> {
           ctx.writeReg8(reg, ctx.fetchByte());
           return DONE;
        });
    }

    /*
    LD r, (HL): Load register (indirect HL)
    Load to the 8-bit register r, data from the absolute address specified by the 16-bit register HL.
    Opcode 0b01xxx110/various
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_R8_HL(R8 reg) {
        return Instruction.create(1, "LD " + reg + " (HL)", ctx -> {
            ctx.writeReg8(reg, ctx.readByte(ctx.readReg16(HL)));
            return DONE;
        });
    }

    /*
    LD (HL), r: Load from register (indirect HL)
    Load to the absolute address specified by the 16-bit register HL, data from the 8-bit register r.
    Opcode 0b01110xxx/various
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_HL_R8(R8 reg) {
        return Instruction.create(1, "LD (HL) " + reg, ctx -> {
            ctx.writeByte(ctx.readReg16(HL), ctx.readReg8(reg));
            return DONE;
        });
    }

    /*
    LD (HL), n: Load from immediate data (indirect HL)
    Load to the absolute address specified by the 16-bit register HL, the immediate data n.
    Opcode 0b00110110/0x36
    Duration 3 machine cycles
    Length 2 bytes: opcode + n
    Flags -
     */
    public static Instruction LD_HL_N() {
        return Instruction.create(2, "LD (HL) n", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchByte());
                        ctx.nextCycle();
                        yield  CONTINUE;
                    }
                    case 1 -> {
                        ctx.writeByte(ctx.readReg16(HL), ctx.mcPop());
                        yield  DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    LD A, (BC): Load accumulator (indirect BC)
    Load to the 8-bit A register, data from the absolute address specified by the 16-bit register BC.
    Opcode 0b00001010/0x0A
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_A_BC() {
        return Instruction.create(1, "LD A (BC)", ctx -> {
            ctx.writeReg8(A, ctx.readByte(ctx.readReg16(BC)));
            return DONE;
        });
    }

    /*
    LD A, (DE): Load accumulator (indirect DE)
    Load to the 8-bit A register, data from the absolute address specified by the 16-bit register DE.
    Opcode 0b00011010/0x1A
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_A_DE() {
        return Instruction.create(1, "LD A (DE)", ctx -> {
            ctx.writeReg8(A, ctx.readByte(ctx.readReg16(DE)));
            return DONE;
        });
    }

    /*
    LD (BC), A: Load from accumulator (indirect BC)
    Load to the absolute address specified by the 16-bit register BC, data from the 8-bit A register.
    Opcode 0b00000010/0x02
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_BC_A() {
        return Instruction.create(1, "LD (BC) A", ctx -> {
            ctx.writeByte(ctx.readReg16(BC), ctx.readReg8(A));
            return DONE;
        });
    }

    /*
    LD (DE), A: Load from accumulator (indirect DE)
    Load to the absolute address specified by the 16-bit register DE, data from the 8-bit A register.
    Opcode 0b00010010/0x12
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_DE_A() {
        return Instruction.create(1, "LD (DE) A", ctx -> {
            ctx.writeByte(ctx.readReg16(DE), ctx.readReg8(A));
            return DONE;
        });
    }

    /*
    LD A, (nn): Load accumulator (direct)
    Load to the 8-bit A register, data from the absolute address specified by the 16-bit operand nn.
    Opcode 0b11111010/0xFA
    Duration 4 machine cycles
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction LD_A_NN() {
        return Instruction.create(3, "LD A (nn)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.fetchByte();
                        ctx.mcPush((hi << 8) | lo);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        ctx.writeReg8(A, ctx.readByte(ctx.mcPop()));
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    LD (nn), A: Load from accumulator (direct)
    Load to the absolute address specified by the 16-bit operand nn, data from the 8-bit A register.
    Opcode 0b11101010/0xEA
    Duration 4 machine cycles
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction LD_NN_A() {
        return Instruction.create(3, "LD (nn) A", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.fetchByte();
                        ctx.mcPush((hi << 8) | lo);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        ctx.writeByte(ctx.mcPop(), ctx.readReg8(A));
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    LDH A, (C): Load accumulator (indirect 0xFF00+C)
    Load to the 8-bit A register, data from the address specified by the 8-bit C register. The full
    16-bit absolute address is obtained by setting the most significant byte to 0xFF and the least
    significant byte to the value of C, so the possible range is 0xFF00-0xFFFF.
    Opcode 0b11110010/0xF2
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LDH_A_C() {
        return Instruction.create(1, "LDH A (C)", ctx -> {
           ctx.writeReg8(A, ctx.readByte(0xFF00 | ctx.readReg8(C)));
            return DONE;
        });
    }

    /*
    LDH (C), A: Load from accumulator (indirect 0xFF00+C)
    Load to the address specified by the 8-bit C register, data from the 8-bit A register. The full
    16-bit absolute address is obtained by setting the most significant byte to 0xFF and the least
    significant byte to the value of C, so the possible range is 0xFF00-0xFFFF.
    Opcode 0b11100010/0xE2
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LDH_C_A() {
        return Instruction.create(1, "LDH (C) A", ctx -> {
            ctx.writeByte(0xFF00 | ctx.readReg8(C), ctx.readReg8(A));
            return DONE;
        });
    }

    /*
    LDH A, (n): Load accumulator (direct 0xFF00+n)
    Load to the 8-bit A register, data from the address specified by the 8-bit immediate data n. The
    full 16-bit absolute address is obtained by setting the most significant byte to 0xFF and the
    least significant byte to the value of n, so the possible range is 0xFF00-0xFFFF.
    Opcode 0b11110000/0xF0
    Duration 3 machine cycles
    Length 2 bytes: opcode + n
    Flags -
     */
    public static Instruction LDH_A_N() {
        return Instruction.create(2 ,("LDH A (n)"), ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(0xFF00 | ctx.fetchByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        ctx.writeReg8(A, ctx.readByte(ctx.mcPop()));
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();

            });
    }

    /*
    LDH (n), A: Load from accumulator (direct 0xFF00+n)
    Load to the address specified by the 8-bit immediate data n, data from the 8-bit A register. The
    full 16-bit absolute address is obtained by setting the most significant byte to 0xFF and the
    least significant byte to the value of n, so the possible range is 0xFF00-0xFFFF.
    Opcode 0b11100000/0xE0
    Duration 3 machine cycles
    Length 2 bytes: opcode + n
    Flags -
     */
    public static Instruction LDH_N_A() {
        return Instruction.create(2, ("LDH (n) A"), ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(0xFF00 | ctx.fetchByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        ctx.writeByte(ctx.mcPop(), ctx.readReg8(A));
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();

                });
    }

    /*
    LD A, (HL-): Load accumulator (indirect HL, decrement)
    Load to the 8-bit A register, data from the absolute address specified by the 16-bit register HL.
    The value of HL is decremented after the memory read.
    Opcode 0b00111010/0x3A
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_A_HLD() {
        return Instruction.create(1, "LD A (HL-)", ctx -> {
            int hl = ctx.readReg16(HL);
            ctx.writeReg8(A, ctx.readByte(hl));
            ctx.writeReg16(HL, ctx.dec16(hl));
            return DONE;
        });
    }

    /*
    LD (HL-), A: Load from accumulator (indirect HL, decrement)
    Load to the absolute address specified by the 16-bit register HL, data from the 8-bit A register.
    The value of HL is decremented after the memory write.
    Opcode 0b00110010/0x32
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_HLD_A() {
        return Instruction.create(1, "LD (HL-) A", ctx -> {
            int hl = ctx.readReg16(HL);
            ctx.writeByte(hl, ctx.readReg8(A));
            ctx.writeReg16(HL, ctx.dec16(hl));
            return DONE;
        });
    }

    /*
    LD A, (HL+): Load accumulator (indirect HL, increment)
    Load to the 8-bit A register, data from the absolute address specified by the 16-bit register HL.
    The value of HL is incremented after the memory read.
    Opcode 0b00101010/0x2A
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_A_HLI() {
        return Instruction.create(1, "LD A (HL+)", ctx -> {
            int hl = ctx.readReg16(HL);
            ctx.writeReg8(A, ctx.readByte(hl));
            ctx.writeReg16(HL, ctx.inc16(hl));
            return DONE;
        });
    }

    /*
    LD (HL+), A: Load from accumulator (indirect HL, increment)
    Load to the absolute address specified by the 16-bit register HL, data from the 8-bit A register.
    The value of HL is incremented after the memory write.
    Opcode 0b00100010/0x22
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_HLI_A() {
        return Instruction.create( 1,"LD (HL+) A", ctx -> {
            int hl = ctx.readReg16(HL);
            ctx.writeByte(hl, ctx.readReg8(A));
            ctx.writeReg16(HL, ctx.inc16(hl));
            return DONE;
        });
    }

    /*
    LD rr, nn: Load 16-bit register / register pair
    Load to the 16-bit register rr, the immediate 16-bit data nn.
    Opcode 0b00xx0001/various
    Duration 3 machine cycles
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction LD_R16_NN(R16 r) {
        return Instruction.create(3, "LD " + r + " nn", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.fetchByte();
                        ctx.writeReg16(r, (hi << 8) | lo);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    LD (nn), SP: Load from stack pointer (direct)
    Load to the absolute address specified by the 16-bit operand nn, data from the 16-bit SP register.
    Opcode 0b00001000/0x08
    Duration 5 machine cycles
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction LD_NN_SP() {
        return Instruction.create(3, "LD (nn) SP", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.fetchByte();
                        ctx.mcPush((hi << 8) | lo);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        int addr = ctx.mcPeek();
                        ctx.writeByte(addr, ctx.getSP() & 0xFF);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 3 -> {
                        int addr = ctx.mcPop();
                        ctx.writeByte(addr + 1, ctx.getSP() >> 8);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    LD SP, HL: Load stack pointer from HL
    Load to the 16-bit SP register, data from the 16-bit HL register.
    Opcode 0b11111001/0xF9
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction LD_SP_HL() {
        return Instruction.create(1, "LD SP HL", ctx -> {
            ctx.tick();
            ctx.setSP(ctx.readReg16(HL));
            return DONE;
        });
    }

    /*
    LD HL, SP+e: Load HL from adjusted stack pointer
    Load to the HL register, 16-bit data calculated by adding the signed 8-bit operand e to the 16-
    bit value of the SP register.
    Opcode 0b11111000/0xF8
    Duration 3 machine cycles
    Length 2 bytes: opcode + e
    Flags Z = 0, N = 0, H = X, C = X
     */
    public static Instruction LD_HL_SP_E() {
        return Instruction.create(2, "LD HL SP+e", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchSignedByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        ctx.tick();
                        var r = ctx.addSP(ctx.mcPop());
                        ctx.writeReg16(HL, r.value());
                        ctx.setFlags(false, false, r.halfCarry(), r.carry());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

}
