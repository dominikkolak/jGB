package cpu;

import cpu.alu.ArithmeticLogicUnit;
import cpu.callback.CycleCallback;
import cpu.control.CPUControl;
import cpu.decoder.Decoder;
import cpu.exception.IllegalOpcodeException;
import cpu.instruction.CycleState;
import cpu.instruction.ExecutionContext;
import cpu.instruction.Instruction;
import cpu.interrupt.InterruptController;
import cpu.register.RegisterFile;
import cpu.register.enums.FLAG;
import cpu.register.enums.INTERRUPT;
import cpu.register.enums.R16;
import cpu.register.enums.R8;
import cpu.state.CPUState;
import shared.Addressable;
import shared.Component;
import snapshot.*;

import java.util.*;

public class CPU implements CPUControl, Component {

    private final RegisterFile registers;
    private final ArithmeticLogicUnit alu;
    private final InterruptController interrupts;
    private final Addressable memory;
    private final CycleCallback callback;

    private final ExecutionContext ctx;

    private int snapshotMemoryStart;
    private int snapshotMemoryEnd;

    private CPUState state;
    private Instruction currentInstruction;
    private boolean haltBug;

    private Instruction lastInstruction;

    public CPU(RegisterFile registers, ArithmeticLogicUnit alu, InterruptController interrupts,
               Addressable memory, CycleCallback callback) {
        this.registers = registers;
        this.alu = alu;
        this.interrupts = interrupts;
        this.memory = memory;
        this.callback = callback;

        this.ctx = new ExecutionContext(registers, registers, memory, alu, callback, this);
        this.state = CPUState.RUNNING;
        this.currentInstruction = null;
        this.haltBug = false;
    }

    public CycleState step() {
        if (state == CPUState.HALTED) {
            return stepHalted();
        }
        if (state == CPUState.STOPPED) {
            return stepStopped();
        }

        if (currentInstruction == null && interrupts.shouldDispatch()) {
            return dispatchInterrupt();
        }

        if (currentInstruction == null) {
            return fetchAndDecode();
        }

        return executeCurrentInstruction();
    }

    private CycleState fetchAndDecode() {
        int pc = registers.getPC();
        int opcode = ctx.fetchByte();

        if (haltBug) {
            registers.setPC(pc);
            haltBug = false;
        }

        if (Decoder.isCBPrefix(opcode)) {
            opcode = ctx.fetchByte();
            currentInstruction = Decoder.decodeCB(opcode);
        } else {
            currentInstruction = Decoder.decode(opcode);
            if (currentInstruction == null) {
                return CycleState.DONE;
            }
        }

        return CycleState.CONTINUE;
    }

    private CycleState executeCurrentInstruction() {
        CycleState result = currentInstruction.execute(ctx);

        switch (result) {
            case DONE -> completeInstruction();
            case HALT -> {
                completeInstruction();
                enterHalt();
            }
            case STOP -> {
                completeInstruction();
                enterStop();
            }
            case CONTINUE -> {}
        }

        return result;
    }

    private void completeInstruction() {

        lastInstruction = currentInstruction;

        currentInstruction = null;
        ctx.reset();

        interrupts.updateIME();
    }

    private CycleState dispatchInterrupt() {
        INTERRUPT interrupt = interrupts.acknowledgeInterrupt();
        if (interrupt == null) {
            return CycleState.DONE;
        }

        int vector = interrupt.vector();
        int pc = registers.getPC();

        ctx.tick();
        ctx.tick();
        ctx.pushToStack((pc >> 8) & 0xFF);
        ctx.pushToStack(pc & 0xFF);
        ctx.tick();
        registers.setPC(vector);

        return CycleState.DONE;
    }

    private void enterHalt() {
        if (interrupts.hasPending() && !interrupts.isIMEnabled()) {
            haltBug = true;
            state = CPUState.RUNNING;
        } else {
            state = CPUState.HALTED;
        }
    }

    private CycleState stepHalted() {
        ctx.tick();

        if (interrupts.hasPending()) {
            state = CPUState.RUNNING;
        }

        return CycleState.DONE;
    }

    private void enterStop() {
        state = CPUState.STOPPED;
    }

    private CycleState stepStopped() {
        if (interrupts.hasPending()) {
            state = CPUState.RUNNING;
        }

        return CycleState.DONE;
    }

    @Override
    public void halt() {
        state = CPUState.HALTED;
    }

    @Override
    public void stop() {
        state = CPUState.STOPPED;
    }

    public void resume() {
        state = CPUState.RUNNING;
    }

    public void forcePC(int address) {
        registers.setPC(address);
    }

    @Override
    public void scheduleEnableInterrupts() {
        interrupts.scheduleEnableIME();
    }

    @Override
    public void disableInterrupts() {
        interrupts.disableIME();
    }

    @Override
    public void enableInterrupts() {
        interrupts.enableIME();
    }

    @Override
    public void reset() {
        registers.reset();
        interrupts.reset();
        ctx.reset();
        state = CPUState.RUNNING;
        currentInstruction = null;
        haltBug = false;
    }

    private static boolean isCBPrefix(int opcode) {
        return (opcode & 0xFF) == 0xCB;
    }

    public CPUState getState() { return state; }
    public RegisterFile getRegisters() { return registers; }
    public InterruptController getInterrupts() { return interrupts; }
    public ExecutionContext getContext() { return ctx; }

    public boolean isRunning() { return state == CPUState.RUNNING; }
    public boolean isHalted() { return state == CPUState.HALTED; }
    public boolean isStopped() { return state == CPUState.STOPPED; }

    public String getCurrentMnemonic() {
        return lastInstruction != null ? lastInstruction.mnemonic() : "---";
    }

    public RegisterSnapshot registerSnapshot() {
        return new RegisterSnapshot(registers.read(R8.A),
                registers.read(R8.B),
                registers.read(R8.C),
                registers.read(R8.D),
                registers.read(R8.E),
                registers.read(R8.H),
                registers.read(R8.L),
                registers.read(R16.SP),
                registers.read(R16.PC));
    }

    public FlagSnapshot flagSnapshot() {
        return new FlagSnapshot(registers.getFlag(FLAG.ZERO),
                registers.getFlag(FLAG.SUBTRACT),
                registers.getFlag(FLAG.HALF_CARRY),
                registers.getFlag(FLAG.CARRY));
    }

    private DisassembledInstruction disassemble(int address) {
        int opcode = memory.read(address) & 0xFF;

        if (Decoder.isCBPrefix(opcode)) {
            int cbOpcode = memory.read(address + 1) & 0xFF;
            Instruction instruction = Decoder.decodeCB(cbOpcode);

            if (instruction == null) {
                return new DisassembledInstruction(
                        address, opcode, List.of(cbOpcode),
                        String.format("CB ???  $%02X", cbOpcode), 2
                );
            }

            return new DisassembledInstruction(
                    address, opcode, List.of(cbOpcode),
                    "CB " + instruction.mnemonic(), 2
            );
        }

        Instruction instruction = Decoder.decode(opcode);

        if (instruction == null) {
            return new DisassembledInstruction(
                    address, opcode, List.of(),
                    String.format("??? $%02X", opcode), 1
            );
        }

        int length = instruction.length();

        List<Integer> operands = new ArrayList<>(length - 1);
        for (int i = 1; i < length; i++) {
            operands.add(memory.read(address + i) & 0xFF);
        }

        String mnemonic = formatMnemonic(instruction.mnemonic(), operands);

        return new DisassembledInstruction(
                address, opcode, List.copyOf(operands), mnemonic, length
        );
    }

    private List<DisassembledInstruction> disassembleAhead(int start) {
        List<DisassembledInstruction> lookahead = new ArrayList<>(10);
        int address = start;

        for (int i = 0; i < 10; i++) {
            try {
                DisassembledInstruction instruction = disassemble(address);
                lookahead.add(instruction);
                address += instruction.length();
            } catch (Exception e) {
                break;
            }
        }

        return List.copyOf(lookahead);
    }

    public InstructionSnapshot instructionSnapshot() {
        int pc = registers.getPC();

        DisassembledInstruction current = disassemble(pc);
        List<DisassembledInstruction> lookahead = disassembleAhead(pc + current.length());

        return new InstructionSnapshot(current, lookahead);
    }

    private String formatMnemonic(String template, List<Integer> operands) {
        if (operands.isEmpty()) {
            return template;
        }

        String result = template;

        if (operands.size() == 1) {
            int value = operands.get(0);

            if (result.contains(" e")) {
                int signed = (byte) value;
                result = result.replace(" e", String.format(" %+d", signed));
            }
            else {
                result = result.replace(" n", String.format(" $%02X", value))
                        .replace(" d", String.format(" $%02X", value));
            }
        }
        else if (operands.size() == 2) {
            int value = operands.get(0) | (operands.get(1) << 8);
            result = result.replace(" nn", String.format(" $%04X", value));
        }

        return result;
    }

    public MemorySnapshot memorySnapshot() {
        int pc = registers.getPC();
        int sp = registers.getSP();

        return new MemorySnapshot(
                captureMemoryWindow(sp, 32),
                captureMemoryWindow(pc - 16, 48),
                captureMemoryWindow(snapshotMemoryStart, snapshotMemoryEnd)
        );
    }

    private Map<Integer, Integer> captureMemoryWindow(int start, int length) {
        Map<Integer, Integer> window = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            int addr = (start + i) & 0xFFFF;
            window.put(addr, memory.read(addr) & 0xFF);
        }
        return Map.copyOf(window);
    }

    public boolean setSnapshotRange(int start, int end) {
        if (start < 0 || end < 0 || start > 0xFFFF || end > 0xFFFF) {
            return false;
        }

        if (end <= start) {
            return false;
        }

        if ((end - start) > 0x0040) {
            return false;
        }

        snapshotMemoryStart = start;
        snapshotMemoryEnd = end;
        return true;
    }
}