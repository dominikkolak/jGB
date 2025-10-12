package cpu.instruction.definitions;

import cpu.instruction.Instruction;
import cpu.register.enums.CONDITION;

import static cpu.instruction.CycleState.CONTINUE;
import static cpu.instruction.CycleState.DONE;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class CallInstructions {

    /*
    CALL nn: Call function
    Unconditional function call to the absolute address specified by the 16-bit operand nn.
    Opcode 0b11001101/0xCD
    Duration 6 machine cycles
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction CALL_NN() {
        return Instruction.create(3, "CALL nn", ctx ->
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
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 3 -> {
                        ctx.pushToStack(ctx.getPC() >> 8);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 4 -> {
                        ctx.pushToStack(ctx.getPC() & 0xFF);
                        ctx.setPC(ctx.mcPop());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    CALL cc, nn: Call function (conditional)
    Conditional function call to the absolute address specified by the 16-bit operand nn, depending
    on the condition cc.
    Note that the operand (absolute address) is read even when the condition is false!
    Opcode 0b110xx100/various
    Duration    6 machine cycles (cc=true)
                3 machine cycles (cc=false)
    Length 3 bytes: opcode + LSB(nn) + MSB(nn)
    Flags -
     */
    public static Instruction CALL_CC_NN(CONDITION cc) {
        return Instruction.create(3, "CALL " + cc + " nn", ctx ->
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
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 3 -> {
                        ctx.pushToStack(ctx.getPC() >> 8);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 4 -> {
                        ctx.pushToStack(ctx.getPC() & 0xFF);
                        ctx.setPC(ctx.mcPop());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    RET: Return from function
    Unconditional return from a function.
    Opcode 0b11001001/0xC9
    Duration 4 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction RET() {
        return Instruction.create(1, "RET", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.popFromStack());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.popFromStack();
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
    RET cc: Return from function (conditional)
    Conditional return from a function, depending on the condition cc.
    Opcode 0b110xx000/various
    Duration    5 machine cycles (cc=true)
                2 machine cycles (cc=false)
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction RET_CC(CONDITION cc) {
        return Instruction.create(1, "RET " + cc, ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.tick();
                        if (ctx.checkCondition(cc)) {
                            ctx.nextCycle();
                            yield CONTINUE;
                        }
                        yield DONE;
                    }
                    case 1 -> {
                        ctx.mcPush(ctx.popFromStack());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.popFromStack();
                        ctx.mcPush((hi << 8) | lo);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 3 -> {
                        ctx.tick();
                        ctx.setPC(ctx.mcPop());
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    RETI: Return from interrupt handler
    Unconditional return from a function. Also enables interrupts by setting IME=1.
    Opcode 0b11011001/0xD9
    Duration 4 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction RETI() {
        return Instruction.create(1, "RETI", ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.popFromStack());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.popFromStack();
                        ctx.mcPush((hi << 8) | lo);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        ctx.tick();
                        ctx.setPC(ctx.mcPop());
                        ctx.enableInterrupts();
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    RST n: Restart / Call function (implied)
    Unconditional function call to the absolute fixed address defined by the opcode.
    Opcode 0b11xxx111/various
    Duration 4 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction RST(int vec) {
        return Instruction.create(1, "RST " + String.format("%02XH", vec), ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.tick();
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        ctx.pushToStack(ctx.getPC() >> 8);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        ctx.pushToStack(ctx.getPC() & 0xFF);
                        ctx.setPC(vec);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

}
