package core;

import cart.Cartridge;
import cpu.CPU;
import cpu.alu.ALU;
import cpu.alu.ArithmeticLogicUnit;
import cpu.instruction.CycleState;
import cpu.interrupt.InterruptController;
import cpu.interrupt.Timer;
import cpu.register.RegisterFile;
import io.InputProvider;
import io.JoyPad;
import io.Serial;
import io.SerialOutputListener;
import mem.DMAController;
import mem.MemoryManagementUnit;
import mtc.MasterTimeController;
import ppu.PPU;
import shared.Component;
import snapshot.Snapshot;

public class Core implements Component {

    private final InterruptController interrupts;
    private final MemoryManagementUnit mmu;
    private final RegisterFile registers;
    private final ArithmeticLogicUnit alu;
    private final CPU cpu;
    private final Timer timer;
    private final PPU ppu;
    private final JoyPad joypad;
    private final Serial serial;
    private final MasterTimeController mtc;

    private boolean paused;
    private Cartridge cartridge;

    public Core() {

        this.interrupts = new InterruptController();
        this.mmu = new MemoryManagementUnit(interrupts);

        this.timer = new Timer(interrupts);
        mmu.connectTimer(timer);

        this.ppu = new PPU(mmu.getVRAM(), mmu.getOAM(), interrupts);
        mmu.connectPpu(ppu);

        this.joypad = new JoyPad(interrupts);
        mmu.connectJoypad(joypad);

        this.serial = new Serial();
        mmu.connectSerial(serial);

        this.mtc = new MasterTimeController();

        mtc.registerComponent(timer);
        mtc.registerComponent(ppu);
        mtc.registerComponent(mmu.getDMA());

        this.registers = new RegisterFile();
        this.alu = new ALU();
        this.cpu = new CPU(registers, alu, interrupts, mmu, mtc::addCycles);

        this.paused = false;
        this.cartridge = null;
    }

    public void loadCartridge(Cartridge cartridge) {
        this.cartridge = cartridge;
        mmu.loadCartridge(cartridge);
        reset();
    }

    public boolean isCartridgeLoaded() {
        return cartridge != null;
    }

    public void setInputProvider(InputProvider provider) {
        joypad.setInputProvider(provider);
    }

    public void setSerialOutputListener(SerialOutputListener listener) {
        serial.setOutputListener(listener);
    }

    public int[] runFrame() {
        if (paused || !isCartridgeLoaded()) {
            return ppu.getFrameBuffer().getFrame();
        }

        mtc.startFrame();

        while (!mtc.isFrameComplete()) {
            cpu.step();
        }

        joypad.update();
        mtc.endFrame();

        return ppu.getFrameBuffer().getFrame();
    }

    public CycleState step() {
        return cpu.step();
    }

    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public void togglePause() { paused = !paused; }
    public boolean isPaused() { return paused; }

    public void setFrameLimitEnabled(boolean enabled) {
        mtc.setFrameLimitEnabled(enabled);
    }

    @Override
    public void reset() {
        registers.reset();
        interrupts.reset();
        mmu.reset();
        timer.reset();
        ppu.reset();
        joypad.reset();
        serial.reset();
        mtc.reset();
        cpu.reset();
        registers.setPC(0x0100);
    }

    public CPU getCPU() { return cpu; }
    public PPU getPPU() { return ppu; }
    public Timer getTimer() { return timer; }
    public RegisterFile getRegisters() { return registers; }
    public InterruptController getInterrupts() { return interrupts; }
    public MemoryManagementUnit getMMU() { return mmu; }
    public MasterTimeController getMTC() { return mtc; }
    public Serial getSerial() { return serial; }
    public JoyPad getJoypad() { return joypad; }
    public Cartridge getCartridge() { return cartridge; }
    public DMAController getDMA() { return mmu.getDMA(); }

    public double getFPS() { return mtc.getCurrentFps(); }
    public long getFrameCount() { return mtc.getFrameCount(); }

    public void setSnapshotRange(int start, int end) {
        cpu.setSnapshotRange(start, end);
    }

    public Snapshot createSnapshot() {
        return new Snapshot(
                cpu.registerSnapshot(),
                cpu.flagSnapshot(),
                cpu.instructionSnapshot(),
                cpu.memorySnapshot(),
                cpu.getInterrupts().createSnapshot(),
                ppu.createSnapshot()
        );
    }
}
