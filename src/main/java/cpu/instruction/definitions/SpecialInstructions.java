package cpu.instruction.definitions;

import cpu.instruction.CycleState;
import cpu.instruction.Instruction;

import static cpu.instruction.CycleState.DONE;

// Credit for Instruction descriptions to:
// Game Boy: Complete Technical Reference by gekkio
// https://gekkio.fi

public class SpecialInstructions {

    /*
    NOP: No operation
    No operation. This instruction doesn’t do anything, but can be used to add a delay of one
    machine cycle and increment PC by one.
    Opcode 0b00000000/0x00
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags -
     */
    public static final Instruction NOP() {
        return Instruction.create(1, "NOP", ctx -> DONE);
    }

    // NO INFO
    public static final Instruction HALT() {
        return Instruction.create(1, "HALT", ctx -> CycleState.HALT);
    }

    // NO INFO
    public static final Instruction STOP() {
        return Instruction.create(1, "STOP", ctx -> {
            ctx.fetchByte();
            return CycleState.STOP;
        });
    }

    /*
    EI: Enable interrupts
    Schedules interrupt handling to be enabled after the next machine cycle.
    Opcode 0b11111011/0xFB
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags -
     */
    public static final Instruction EI() {
        return Instruction.create(1, "EI", ctx -> {
            ctx.scheduleInterruptEnable();
            return DONE;
        });
    }

    /*
    DI: Disable interrupts
    Disables interrupt handling by setting IME=0 and cancelling any scheduled effects of the EI
    instruction if any.
    Opcode 0b11110011/0xF3
    Duration 1 machine cycle
    Length 1 byte: opcode
    Flags -
     */
    public static final Instruction DI() {
        return Instruction.create(1, "DI", ctx -> {
            ctx.disableInterrupts();
            return DONE;
        });
    }

}
