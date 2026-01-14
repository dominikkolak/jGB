package cpu.instruction.definitions;

import cpu.instruction.Instruction;
import cpu.register.enums.R8;

import static cpu.instruction.CycleState.*;
import static cpu.register.enums.R16.*;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class BitInstructions {

    /*
    BIT b, r: Test bit (register)
    Tests the bit b of the 8-bit register r.
    The zero flag is set to 1 if the chosen bit is 0, and 0 otherwise.
    Opcode 0b01xxxxxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 1
     */
    public static Instruction BIT_N_R8(int n, R8 r) {
        return Instruction.create(2, "BIT " + n + " " + r, ctx -> {
            ctx.setFlagsBit(ctx.bit(ctx.readReg8(r), n));
            return DONE;
        });
    }

    /*
    BIT b, (HL): Test bit (indirect HL)
    Tests the bit b of the 8-bit data at the absolute address specified by the 16-bit register HL.
    The zero flag is set to 1 if the chosen bit is 0, and 0 otherwise.
    Opcode 0b01xxx110/various
    Duration 3 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags Z = X, N = 0, H = 1
     */
    public static Instruction BIT_N_HL(int n) {
        return Instruction.create(2, "BIT " + n + " (HL)", ctx -> {
            ctx.setFlagsBit(ctx.bit(ctx.readByte(ctx.readReg16(HL)), n));
            return DONE;
        });
    }


    /*
    SET b, r: Set bit (register)
    Sets the bit b of the 8-bit register r to 1.
    Opcode 0b11xxxxxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags -
     */
    public static Instruction SET_N_R8(int n, R8 r) {
        return Instruction.create(2, "SET " + n + " " + r, ctx -> {
            ctx.writeReg8(r, ctx.set(ctx.readReg8(r), n));
            return DONE;
        });
    }

    /*
    SET b, (HL): Set bit (indirect HL)
    Sets the bit b of the 8-bit data at the absolute address specified by the 16-bit register HL, to 1.
    Opcode 0b11xxx110/various
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags -
     */
    public static Instruction SET_N_HL(int n) {
        return Instruction.create(2, "SET " + n + " (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int val = ctx.set(ctx.mcPop(), n);
                        ctx.writeByte(ctx.mcPop(), val);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    RES b, r: Reset bit (register)
    Resets the bit b of the 8-bit register r to 0.
    Opcode 0b10xxxxxx/various
    Duration 2 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags -
     */
    public static Instruction RES_N_R8(int n, R8 r) {
        return Instruction.create(2, "RES " + n + " " + r, ctx -> {
            ctx.writeReg8(r, ctx.res(ctx.readReg8(r), n));
            return DONE;
        });
    }

    /*
    RES b, (HL): Reset bit (indirect HL)
    Resets the bit b of the 8-bit data at the absolute address specified by the 16-bit register HL, to 0.
    Opcode 0b10xxx110/various
    Duration 4 machine cycles
    Length 2 bytes: CB prefix + opcode
    Flags -
     */
    public static Instruction RES_N_HL(int n) {
        return Instruction.create(2, "RES " + n + " (HL)", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int addr = ctx.readReg16(HL);
                        ctx.mcPush(addr);
                        ctx.mcPush(ctx.readByte(addr));
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int val = ctx.res(ctx.mcPop(), n);
                        ctx.writeByte(ctx.mcPop(), val);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

}
