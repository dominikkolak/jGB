package cpu.cpu.instruction;

import cpu.alu.ALU;
import cpu.alu.ArithmeticLogicUnit;
import cpu.alu.result.ALUR16;
import cpu.alu.result.ALUR8;
import cpu.alu.result.FLAGR;
import cpu.callback.CycleCallback;
import cpu.control.CPUControl;
import cpu.register.FlagOperations;
import cpu.register.Registers;
import cpu.register.enums.CONDITION;
import cpu.register.enums.FLAG;
import cpu.register.enums.R16;
import cpu.register.enums.R8;
import shared.Addressable;

public class ExecutionContext {

    private final Registers registers;
    private final FlagOperations flags;
    private final Addressable memory;
    private final ArithmeticLogicUnit alu;
    private final CycleCallback callback;
    private final CPUControl control;

    private int cycle;

    private final int[] mcStack = new int[4];
    private int mcSP;

    public ExecutionContext(Registers registers, FlagOperations flags, Addressable memory,
                            ArithmeticLogicUnit alu, CycleCallback callback, CPUControl control) {
        this.registers = registers;
        this.flags = flags;
        this.memory = memory;
        this.alu = alu;
        this.callback = callback;
        this.control = control;
    }

    public int cycle() { return cycle; }
    public void nextCycle() { cycle++; }

    public void reset() {
        cycle = 0;
        mcSP = 0;
    }

    public void mcPush(int value) { mcStack[mcSP++] = value; }
    public int mcPop() { return mcStack[--mcSP]; }
    public int mcPeek() { return mcStack[mcSP - 1]; }
    public int mcPeek(int index) { return mcStack[index]; }

    public int fetchByte() {
        callback.consumeCycles(4);
        return memory.read8(registers.getAndIncPC());
    }

    public int fetchSignedByte() {
        callback.consumeCycles(4);
        return memory.read8Signed(registers.getAndIncPC());
    }

    public int readByte(int address) {
        callback.consumeCycles(4);
        return memory.read8(address);
    }

    public void writeByte(int address, int value) {
        callback.consumeCycles(4);
        memory.write8(address, value);
    }

    public void tick() {
        callback.consumeCycles(4);
    }

    public void pushToStack(int value) {
        writeByte(registers.decAndGetSP(), value);
    }

    public int popFromStack() {
        return readByte(registers.getAndIncSP());
    }

    public int readReg8(R8 r) { return registers.read(r); }
    public void writeReg8(R8 r, int v) { registers.write(r, v); }

    public int readReg16(R16 r) { return registers.read(r); }
    public void writeReg16(R16 r, int v) { registers.write(r, v); }

    public int getPC() { return registers.getPC(); }
    public void setPC(int v) { registers.setPC(v); }
    public void addToPC(int offset) { registers.addToPC(offset); }

    public int getSP() { return registers.getSP(); }
    public void setSP(int v) { registers.setSP(v); }

    public boolean getFlag(FLAG f) { return flags.getFlag(f); }
    public void setFlag(FLAG f, boolean v) { flags.setFlag(f, v); }

    public boolean carry() { return flags.getFlag(FLAG.CARRY); }
    public boolean zero() { return flags.getFlag(FLAG.ZERO); }
    public boolean halfCarry() { return flags.getFlag(FLAG.HALF_CARRY); }
    public boolean subtract() { return flags.getFlag(FLAG.SUBTRACT); }

    public void setFlags(ALUR8 r) { flags.setFlags(r); }
    public void setFlags(Boolean z, Boolean n, Boolean h, Boolean c) { flags.setFlags(z, n, h, c); }

    public void setFlagsBit(FLAGR r) { flags.setFlags(r.zero(), r.subtract(), r.halfCarry(), null); }

    public boolean checkCondition(CONDITION c) { return flags.checkCondition(c); }

    public ALU alu() { return (ALU) alu; }

    public ALUR8 add(int a, int b) { return alu.add8(a, b); }
    public ALUR8 adc(int a, int b) { return alu.adc(a, b, carry()); }
    public ALUR8 sub(int a, int b) { return alu.sub(a, b); }
    public ALUR8 sbc(int a, int b) { return alu.sbc(a, b, carry()); }
    public ALUR8 cp(int a, int b) { return alu.cp(a, b); }
    public ALUR8 inc(int a) { return alu.inc8(a); }
    public ALUR8 dec(int a) { return alu.dec8(a); }

    public ALUR8 and(int a, int b) { return alu.and(a, b); }
    public ALUR8 or(int a, int b) { return alu.or(a, b); }
    public ALUR8 xor(int a, int b) { return alu.xor(a, b); }
    public ALUR8 cpl(int a) { return alu.cpl(a); }

    public ALUR16 add16(int a, int b) { return alu.add16(a, b); }
    public ALUR8 addSP(int offset) { return alu.addSigned(getSP(), offset); }
    public int inc16(int a) { return alu.inc16(a); }
    public int dec16(int a) { return alu.dec16(a); }

    public ALUR8 rlc(int a) { return alu.rlc(a); }
    public ALUR8 rrc(int a) { return alu.rrc(a); }
    public ALUR8 rl(int a) { return alu.rl(a, carry()); }
    public ALUR8 rr(int a) { return alu.rr(a, carry()); }
    public ALUR8 sla(int a) { return alu.sla(a); }
    public ALUR8 sra(int a) { return alu.sra(a); }
    public ALUR8 srl(int a) { return alu.srl(a); }
    public ALUR8 swap(int a) { return alu.swap(a); }

    public FLAGR bit(int a, int bit) { return alu.bit(a, bit); }
    public int set(int a, int bit) { return alu.set(a, bit); }
    public int res(int a, int bit) { return alu.rst(a, bit); }

    public ALUR8 daa(int a) { return alu.daa(a, subtract(), halfCarry(), carry()); }

    public void scheduleInterruptEnable() { control.scheduleEnableInterrupts(); }
    public void enableInterrupts() { control.enableInterrupts(); }
    public void disableInterrupts() { control.disableInterrupts(); }

}
