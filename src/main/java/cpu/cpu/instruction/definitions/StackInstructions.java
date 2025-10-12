package cpu.cpu.instruction.definitions;

import cpu.instruction.Instruction;
import cpu.register.enums.R16;

import static cpu.instruction.CycleState.CONTINUE;
import static cpu.instruction.CycleState.DONE;
import static cpu.register.enums.R16.AF;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class StackInstructions {

    /*
    PUSH rr: Push to stack
    Push to the stack memory, data from the 16-bit register rr.
    Opcode 0b11xx0101/various
    Duration 4 machine cycles
    Length 1 byte: opcode
    Flags -
     */
    public static Instruction PUSH_R16(R16 r) {
        return Instruction.create(1, "PUSH " + r, ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.tick();
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        ctx.pushToStack(ctx.readReg16(r) >> 8);
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 2 -> {
                        ctx.pushToStack(ctx.readReg16(r) & 0xFF);
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

    /*
    POP rr: Pop from stack
    Pops to the 16-bit register rr, data from the stack memory.
    This instruction does not do calculations that affect flags, but POP AF completely replaces the
    F register value, so all flags are changed based on the 8-bit data that is read from memory.
    Opcode 0b11xx0001/various
    Duration 3 machine cycles
    Length 1 byte: opcode
    Flags See the instruction description
     */
    public static Instruction POP_R16(R16 r) {
        return Instruction.create(1, "POP " + r, ctx ->
                switch (ctx.cycle()) {
                    case 0 -> {
                        ctx.mcPush(ctx.popFromStack());
                        ctx.nextCycle();
                        yield CONTINUE;
                    }
                    case 1 -> {
                        int lo = ctx.mcPop();
                        int hi = ctx.popFromStack();
                        if (r == AF) {
                            ctx.writeReg16(AF, (hi << 8) | (lo & 0xF0));
                        } else {
                            ctx.writeReg16(r, (hi << 8) | lo);
                        }
                        yield DONE;
                    }
                    default -> throw new IllegalStateException();
                });
    }

}
