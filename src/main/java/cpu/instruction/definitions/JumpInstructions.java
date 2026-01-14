package cpu.instruction.definitions;

import cpu.instruction.Instruction;
import cpu.register.enums.CONDITION;

import static cpu.instruction.CycleState.*;
import static cpu.register.enums.R16.*;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class JumpInstructions {

    /*
    JP nn: Jump
    Unconditional jump to the absolute address specified by the 16-bit immediate operand nn.
    Opcode 0b11000011/0xC3
    Duration 4 machine cycles
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction JP_NN() {
        return Instruction.create(3, "JP nn", ctx ->
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
                        ctx.tick();
                        ctx.setPC(ctx.mcPop());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }


    /*
    JP cc, nn: Jump (conditional)
    Conditional jump to the absolute address specified by the 16-bit operand nn, depending on the
    condition cc.

    In some documentation this instruction is written as JP [HL]. This is very misleading,
    since brackets are usually used to indicate a memory read, and this instruction simply
    copies the value of HL to PC.

    Note that the operand (absolute address) is read even when the condition is false!

    Opcode 0b110xx010/various
    Duration    4 machine cycles (cc=true)
                3 machine cycles (cc=false)
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction JP_CC_NN(CONDITION cc) {
        return Instruction.create(3, "JP " + cc + " nn", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.fetchByte();
                        if (ctx.checkCondition(cc)) {
                            ctx.mcPush((hi << 8) | lo);
                            ctx.nextCycle();
                            yield CONTINUE;
                        }
                        yield DONE;
                    }
                    case 2 -> {
                        ctx.tick();
                        ctx.setPC(ctx.mcPop());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    JP HL: Jump to HL
    Unconditional jump to the absolute address specified by the 16-bit register HL.
    Opcode 0b11101001/0xE9
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction JP_HL() {
        return Instruction.create(1, "JP HL", ctx -> {
            ctx.setPC(ctx.readReg16(HL));
            return DONE;
        });
    }

    /*
    JR e: Relative jump
    Unconditional jump to the relative address specified by the signed 8-bit operand e.
    Opcode 0b00011000/0x18
    Duration 3 machine cycles
    Length 2 bytes: opcode + e
    Flags -
     */
    public static Instruction JR_E() {
        return Instruction.create(2, "JR e", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.fetchSignedByte());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        ctx.tick();
                        ctx.addToPC(ctx.mcPop());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    JR cc, e: Relative jump (conditional)
    Conditional jump to the relative address specified by the signed 8-bit operand e, depending on
    the condition cc.
    Note that the operand (relative address offset) is read even when the condition is false!
    Opcode 0b001xx000/various
    Duration    3 machine cycles (cc=true)
                2 machine cycles (cc=false)
    Length 2 bytes: opcode + e
    Flags -
     */
    public static Instruction JR_CC_E(CONDITION cc) {
        return Instruction.create(2, "JR " + cc + " e", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        int offset = ctx.fetchSignedByte();
                        if (ctx.checkCondition(cc)) {
                            ctx.mcPush(offset);
                            ctx.nextCycle();
                            yield CONTINUE;
                        }
                        yield DONE;
                    }
                    case 1 -> {
                        ctx.tick();
                        ctx.addToPC(ctx.mcPop());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

}
