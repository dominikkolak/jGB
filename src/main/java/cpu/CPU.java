package cpu;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.decoder.OpcodeDecoder;
import cpu.instructions.Instruction;
import cpu.instructions.types.MiscInstruction;
import cpu.interfaces.BUS;
import cpu.interfaces.InstructionDecoder;
import cpu.state.CpuState;
import cpu.state.InterruptState;
import cpu.state.TimingState;
import shared.Component;

public class CPU implements Component {

    private final RegisterFile registers;
    private final ALU alu;
    private final InstructionDecoder decoder;
    private final InterruptState interruptState;
    private final TimingState timingState;

    private CpuState state;

    public CPU() {
        this.registers = new RegisterFile();
        this.alu = new ALU(registers.getFlags());
        this.decoder = new OpcodeDecoder();
        this.interruptState = new InterruptState();
        this.timingState = new TimingState();
        this.state = CpuState.RUNNING;
    }

    @Override
    public void tick(int cycles) {
        timingState.tick(cycles);
        interruptState.tick(cycles);
    }

    @Override
    public void reset() {
        registers.reset();
        alu.reset();
        interruptState.reset();
        timingState.reset();
        state = CpuState.RUNNING;
    }

    @Override
    public String getComponentName() {
        return "SM83 CPU";
    }

    public int step(BUS bus) {
        if (state != CpuState.RUNNING) {
            // TODO: Interupt to wake up form HALT/STOP
            return 1; // 1 cylce while halt
        }

        // F
        int pc = registers.getProgramCounter().getValue();
        Instruction instruction = decoder.decode(pc, bus);

        // A PC by I Size
        registers.getProgramCounter().increment(instruction.getSize());

        // E
        int cycles = instruction.execute(registers, alu, bus);

        // HALT
        if (instruction instanceof MiscInstruction) {
            MiscInstruction misc = (MiscInstruction) instruction;
            if (misc.affectsCpuState()) {
                state = misc.getTargetState();
            }
        }

        // U T and IS
        timingState.addCycles(cycles);
        interruptState.tick(cycles);

        return cycles;
    }

    public void run(BUS bus, int cyclesToRun) {
        int cyclesExecuted = 0;

        while (cyclesExecuted < cyclesToRun && state == CpuState.RUNNING) {
            cyclesExecuted += step(bus);
        }
    }

    public RegisterFile getRegisters() {return registers;}

    public ALU getAlu() {return alu;}

    public InterruptState getInterruptState() {return interruptState;}

    public TimingState getTimingState() {return timingState;}

    public CpuState getState() {return state;}

    public void setState(CpuState state) {this.state = state;}

    public long getTotalCycles() {return timingState.getTotalCycles();}

}
