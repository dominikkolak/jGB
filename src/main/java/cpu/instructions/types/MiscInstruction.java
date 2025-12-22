package cpu.instructions.types;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.instructions.Instruction;
import cpu.interfaces.BUS;
import cpu.state.CpuState;

/// Only NOP and HALT implemented!!!!!!!!!!

public class MiscInstruction implements Instruction {

    private enum MiscType {
        NOP, HALT
    }

    private final MiscType type;
    private CpuState targetState;

    private MiscInstruction(MiscType type) {
        this.type = type;
        this.targetState = null;
    }

    public static MiscInstruction nop() {
        return new MiscInstruction(MiscType.NOP);
    }

    public static MiscInstruction halt() {
        MiscInstruction inst = new MiscInstruction(MiscType.HALT);
        inst.targetState = CpuState.HALTED;
        return inst;
    }

    @Override
    public int execute(RegisterFile registers, ALU alu, BUS bus) {
        // State change by step
        return 1; // 4 cycles
    }

    @Override
    public String getMnemonic() {
        return type == MiscType.NOP ? "NOP" : "HALT";
    }

    @Override
    public int getSize() {
        return 1;
    }

    public CpuState getTargetState() {
        return targetState;
    }

    public boolean affectsCpuState() {
        return targetState != null;
    }

}
