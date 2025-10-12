package cpu.instruction.definitions;

import cpu.instruction.Instruction;
import cpu.register.enums.R16;
import cpu.register.enums.R8;

import static cpu.instruction.CycleState.CONTINUE;
import static cpu.instruction.CycleState.DONE;
import static cpu.register.enums.R16.HL;
import static cpu.register.enums.R8.A;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class ArithmeticInstructions {

    /*
    ADD r: Add (register)
    Adds to the 8-bit A register, the 8-bit register r, and stores the result back into the A register.
    Opcode 0b10000xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = X, C = X
     */
    public static Instruction ADD_A_R8(R8 r) {
        return Instruction.create(1, "ADD A " + r, ctx -> {
            var res = ctx.add(ctx.readReg8(A), ctx.readReg8(r));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    ADD (HL): Add (indirect HL)
    Adds to the 8-bit A register, data from the absolute address specified by the 16-bit register HL,
    and stores the result back into the A register.
    Opcode 0b10000110/0x86
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = X, C = X
     */
    public static Instruction ADD_A_HL() {
        return Instruction.create(1, "ADD A (HL)", ctx -> {
            var res = ctx.add(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL)));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    ADD n: Add (immediate)
    Adds to the 8-bit A register, the immediate data n, and stores the result back into the A register.
    Opcode 0b11000110/0xC6
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = X, N = 0, H = X, C = X
     */
    public static Instruction ADD_A_N() {
        return Instruction.create(2, "ADD A n", ctx -> {
            var res = ctx.add(ctx.readReg8(A), ctx.fetchByte());
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    ADC r: Add with carry (register)
    Adds to the 8-bit A register, the carry flag and the 8-bit register r, and stores the result back
    into the A register.
    Opcode 0b10001xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = X, C = X
     */
    public static Instruction ADC_A_R8(R8 r) {
        return Instruction.create(1, "ADC A " + r, ctx -> {
            var res = ctx.adc(ctx.readReg8(A), ctx.readReg8(r));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    ADC (HL): Add with carry (indirect HL)
    Adds to the 8-bit A register, the carry flag and data from the absolute address specified by the
    16-bit register HL, and stores the result back into the A register.
    Opcode 0b10001110/0x8E
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = X, C = X
     */
    public static Instruction ADC_A_HL() {
        return Instruction.create(1, "ADC A (HL)", ctx -> {
            var res = ctx.adc(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL)));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    ADC n: Add with carry (immediate)
    Adds to the 8-bit A register, the carry flag and the immediate data n, and stores the result back
    into the A register.
    Opcode 0b11001110/0xCE
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = X, N = 0, H = X, C = X
     */
    public static Instruction ADC_A_N() {
        return Instruction.create(2, "ADC A n", ctx -> {
            var res = ctx.adc(ctx.readReg8(A), ctx.fetchByte());
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SUB r: Subtract (register)
    Subtracts from the 8-bit A register, the 8-bit register r, and stores the result back into the A
    register.
    Opcode 0b10010xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction SUB_R8(R8 r) {
        return Instruction.create(1, "SUB " + r, ctx -> {
            var res = ctx.sub(ctx.readReg8(A), ctx.readReg8(r));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SUB (HL): Subtract (indirect HL)
    Subtracts from the 8-bit A register, data from the absolute address specified by the 16-bit
    register HL, and stores the result back into the A register.
    Opcode 0b10010110/0x96
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction SUB_HL() {
        return Instruction.create(1, "SUB (HL)", ctx -> {
            var res = ctx.sub(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL)));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SUB n: Subtract (immediate)
    Subtracts from the 8-bit A register, the immediate data n, and stores the result back into the A
    register.
    Opcode 0b11010110/0xD6
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction SUB_N() {
        return Instruction.create(2, "SUB n", ctx -> {
            var res = ctx.sub(ctx.readReg8(A), ctx.fetchByte());
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SBC r: Subtract with carry (register)
    Subtracts from the 8-bit A register, the carry flag and the 8-bit register r, and stores the result
    back into the A register.
    Opcode 0b10011xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction SBC_A_R8(R8 r) {
        return Instruction.create(1, "SBC A " + r, ctx -> {
            var res = ctx.sbc(ctx.readReg8(A), ctx.readReg8(r));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SBC (HL): Subtract with carry (indirect HL)
    Subtracts from the 8-bit A register, the carry flag and data from the absolute address specified
    by the 16-bit register HL, and stores the result back into the A register.
    Opcode 0b10011110/0x9E
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction SBC_A_HL() {
        return Instruction.create(1, "SBC A (HL)", ctx -> {
            var res = ctx.sbc(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL)));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    SBC n: Subtract with carry (immediate)
    Subtracts from the 8-bit A register, the carry flag and the immediate data n, and stores the
    result back into the A register.
    Opcode 0b11011110/0xDE
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction SBC_A_N() {
        return Instruction.create(2, "SBC A n", ctx -> {
            var res = ctx.sbc(ctx.readReg8(A), ctx.fetchByte());
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res);
            return DONE;
        });
    }

    /*
    CP r: Compare (register)
    Subtracts from the 8-bit A register, the 8-bit register r, and updates flags based on the result.
    This instruction is basically identical to SUB r, but does not update the A register.
    Opcode 0b10111xxx/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction CP_R8(R8 r) {
        return Instruction.create(1, "CP " + r, ctx -> {
            ctx.setFlags(ctx.cp(ctx.readReg8(A), ctx.readReg8(r)));
            return DONE;
        });
    }

    /*
    CP (HL): Compare (indirect HL)
    Subtracts from the 8-bit A register, data from the absolute address specified by the 16-bit
    register HL, and updates flags based on the result. This instruction is basically identical to SUB
    (HL), but does not update the A register.
    Opcode 0b10111110/0xBE
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction CP_HL() {
        return Instruction.create(1, "CP (HL)", ctx -> {
            ctx.setFlags(ctx.cp(ctx.readReg8(A), ctx.readByte(ctx.readReg16(HL))));
            return DONE;
        });
    }

    /*
    CP n: Compare (immediate)
    Subtracts from the 8-bit A register, the immediate data n, and updates flags based on the result.
    This instruction is basically identical to SUB n, but does not update the A register.
    Opcode 0b11111110/0xFE
    Duration 2 machine cycles
    Length 2 bytes: opcode + n
    Flags Z = X, N = 1, H = X, C = X
     */
    public static Instruction CP_N() {
        return Instruction.create(2, "CP n", ctx -> {
            ctx.setFlags(ctx.cp(ctx.readReg8(A), ctx.fetchByte()));
            return DONE;
        });
    }

    /*
    INC r: Increment (register)
    Increments data in the 8-bit register r.
    Opcode 0b00xxx100/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = X
     */
    public static Instruction INC_R8(R8 r) {
        return Instruction.create(1, "INC " + r, ctx -> {
            var res = ctx.inc(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res.zero(), res.subtract(), res.halfCarry(), null);
            return DONE;
        });
    }

    /*
    INC (HL): Increment (indirect HL)
    Increments data at the absolute address specified by the 16-bit register HL.
    Opcode 0b00110100/0x34
    Duration 3 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 0, H = X
     */
    public static Instruction INC_HL() {
        return Instruction.create( 1,"INC (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.inc(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res.zero(), res.subtract(), res.halfCarry(), null);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    DEC r: Decrement (register)
    Decrements data in the 8-bit register r.
    Opcode 0b00xxx101/various
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X
     */
    public static Instruction DEC_R8(R8 r) {
        return Instruction.create(1, "DEC " + r, ctx -> {
            var res = ctx.dec(ctx.readReg8(r));
            ctx.writeReg8(r, res.value());
            ctx.setFlags(res.zero(), res.subtract(), res.halfCarry(), null);
            return DONE;
        });
    }

    /*
    DEC (HL): Decrement (indirect HL)
    Decrements data at the absolute address specified by the 16-bit register HL.
    Opcode 0b00110101/0x35
    Duration 3 machine cycles
    Length 1 byte: opcode
    Flags Z = X, N = 1, H = X
     */
    public static Instruction DEC_HL() {
        return Instruction.create(1, "DEC (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        var res = ctx.dec(ctx.mcPop());
                        ctx.writeByte(ctx.mcPop(), res.value());
                        ctx.setFlags(res.zero(), res.subtract(), res.halfCarry(), null);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    ADD HL, rr: Add (16-bit register)
    Adds to the 16-bit HL register pair, the 16-bit register rr, and stores the result back into the HL
    register pair.
    Opcode 0b00xx1001/various
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags N = 0, H = X, C = X
     */
    public static Instruction ADD_HL_R16(R16 r) {
        return Instruction.create(1, "ADD HL " + r, ctx -> {
            ctx.tick();
            var res = ctx.add16(ctx.readReg16(HL), ctx.readReg16(r));
            ctx.writeReg16(HL, res.value());
            ctx.setFlags(null, false, res.halfCarry(), res.carry());
            return DONE;
        });
    }

    /*
    ADD SP, e: Add to stack pointer (relative)
    Loads to the 16-bit SP register, 16-bit data calculated by adding the signed 8-bit operand e to
    the 16-bit value of the SP register.
    Opcode 0b11101000/0xE8
    Duration 4 machine cycles
    Length 2 bytes: opcode + e
    Flags Z = 0, N = 0, H = X, C = X
     */
    public static Instruction ADD_SP_E() {
        return Instruction.create(2, "ADD SP e", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchSignedByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        ctx.tick();
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        ctx.tick();
                        var res = ctx.addSP(ctx.mcPop());
                        ctx.setSP(res.value());
                        ctx.setFlags(false, false, res.halfCarry(), res.carry());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    INC rr: Increment 16-bit register
    Increments data in the 16-bit register rr.
    Opcode 0b00xx0011/various
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction INC_R16(R16 r) {
        return Instruction.create(1, "INC " + r, ctx -> {
            ctx.tick();
            ctx.writeReg16(r, ctx.inc16(ctx.readReg16(r)));
            return DONE;
        });
    }

    /*
    DEC rr: Decrement 16-bit register
    Decrements data in the 16-bit register rr.
    Opcode 0b00xx1011/various
    Duration 2 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction DEC_R16(R16 r) {
        return Instruction.create(1, "DEC " + r, ctx -> {
            ctx.tick();
            ctx.writeReg16(r, ctx.dec16(ctx.readReg16(r)));
            return DONE;
        });
    }

    /*
    DAA: Decimal adjust accumulator
    Opcode 0b00100111/0x27
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags Z = X, H = 0, C = X
     */
    public static Instruction DAA() {
        return Instruction.create(1, "DAA", ctx -> {
            var res = ctx.daa(ctx.readReg8(A));
            ctx.writeReg8(A, res.value());
            ctx.setFlags(res.zero(), null, false, res.carry());
            return DONE;
        });
    }

    /*
    CPL: Complement accumulator
    Flips all the bits in the 8-bit A register, and sets the N and H flags.
    Opcode 0b00101111/0x2F
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags N = 1, H = 1
     */
    public static Instruction CPL() {
        return Instruction.create(1, "CPL", ctx -> {
            ctx.writeReg8(A, ctx.readReg8(A) ^ 0xFF);
            ctx.setFlags(null, true, true, null);
            return DONE;
        });
    }

    /*
    SCF: Set carry flag
    Sets the carry flag, and clears the N and H flags.
    Opcode 0b00110111/0x37
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags N = 0, H = 0, C = 1
     */
    public static Instruction SCF() {
        return Instruction.create(1, "SCF", ctx -> {
            ctx.setFlags(null, false, false, true);
            return DONE;
        });
    }

    /*
    CCF: Complement carry flag
    Flips the carry flag, and clears the N and H flags.
    Opcode 0b00111111/0x3F
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags N = 0, H = 0, C = X
     */
    public static Instruction CCF() {
        return Instruction.create(1, "CCF", ctx -> {
            ctx.setFlags(null, false, false, !ctx.carry());
            return DONE;
        });
    }

}
