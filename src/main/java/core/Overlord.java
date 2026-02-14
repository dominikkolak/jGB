package core;

import cart.Cartridge;
import cpu.CPU;
import cpu.alu.ALU;
import cpu.alu.ArithmeticLogicUnit;
import cpu.interrupt.InterruptController;
import cpu.register.RegisterFile;
import io.InputProvider;
import io.SerialOutputListener;
import mem.MemoryManagementUnit;
import mtc.MasterTimeController;
import ppu.FrameConstants;
import snapshot.Snapshot;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;

public class Overlord implements Runnable {

    private Core emulator;
    private volatile boolean running = true;

    private Cartridge cart;

    private final int[] primaryBuffer = new int[160 * 144];
    private final int[] secondaryBuffer = new int[160 * 144];
    private volatile int[] frameBuffer = primaryBuffer;

    private volatile int snapshotInterval = 1;
    private int framesSinceSnapshot = 0;

    private byte[] saveData;

    private final AtomicReference<Snapshot> latestSnapshot = new AtomicReference<>();

    public Overlord() {
        this.emulator = new Core();
        this.cart = null;

        emulator.setBreakpointListener(this::onBreakpoint);
    }

    @Override
    public void run() {

        int[] backBuffer = secondaryBuffer;

        while (running) {
            if (!emulator.isPaused() && emulator.isCartridgeLoaded()) {
                int[] frame = emulator.runFrame();
                System.arraycopy(frame, 0, backBuffer, 0, frame.length);

                frameBuffer = backBuffer;
                backBuffer = (backBuffer == primaryBuffer) ? secondaryBuffer : primaryBuffer;

                if (emulator.isDebugModeEnabled()) {
                    framesSinceSnapshot++;
                    if (framesSinceSnapshot >= snapshotInterval) {
                        createSnapshot();
                        framesSinceSnapshot = 0;
                    }
                }

            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    public int[] getFrame() {
        return frameBuffer;
    }

    private void flushFrameBuffer() {
        Arrays.fill(primaryBuffer, 0);
        Arrays.fill(secondaryBuffer, 0);
        frameBuffer = primaryBuffer;
    }

    public void loadCartridge(byte[] rom) {
        cart = new Cartridge(rom);
        emulator.loadCartridge(cart);

        if (emulator.isCartridgeLoaded()) {
            createSnapshot();
        }
    }

    public void setFrameLimitEnabled(boolean select) {
        emulator.setFrameLimitEnabled(select);
    }

    public long getFrameCount() {
        return emulator.getFrameCount();
    }

    public boolean isPaused() {
        return emulator.isPaused();
    }

    public void reset() {
        emulator.reset();
        emulator.pause();
    }

    public boolean isCartridgeLoaded() {
        return emulator.isCartridgeLoaded();
    }

    public void togglePause() {
        emulator.togglePause();

        if (emulator.isPaused() && emulator.isCartridgeLoaded()) {
            createSnapshot();
        }
    }

    public void setInputProvider(InputProvider input) {
        emulator.setInputProvider(input);
    }

    private void createSnapshot() {
        latestSnapshot.set(emulator.createSnapshot());
    }

    public Snapshot getSnapshot() {
        return latestSnapshot.get();
    }

    public void setDebugMode(boolean enabled) {
        emulator.setDebugMode(enabled);

        if (!enabled) {
            latestSnapshot.set(null);
        }
    }

    public boolean isDebugModeEnabled() {
        return emulator.isDebugModeEnabled();
    }


    public void setSnapshotInterval(int frames) {
        this.snapshotInterval = Math.max(1, frames);
    }

    public void setSnapshotRange(int start, int end) {
        emulator.setSnapshotRange(start, end);
    }

    public void forceSnapshot() {
        if (emulator.isCartridgeLoaded()) {
            createSnapshot();
        }
    }

    public void onBreakpoint() {
        createSnapshot();
    }

    public void step() {
        if (emulator.isCartridgeLoaded() && emulator.isPaused()) {
            emulator.step();
            createSnapshot();
        }
    }

    public void addBreakpoint(int address) {
        emulator.addBreakpoint(address);
    }

    public void removeBreakpoint(int address) {
        emulator.removeBreakpoint(address);
    }

    public void setBreakpointEnabled(int address, boolean enabled) {
        emulator.setBreakpointEnabled(address, enabled);
    }

    public Map<Integer, Boolean> getBreakpoints() {
        return emulator.getBreakpoints();
    }

    public void clearBreakpoints() {
        emulator.clearBreakpoints();
    }

    public Core getCore() {
        return emulator;
    }

    public void loadSaveData(byte[] saveData) {
        this.saveData = Arrays.copyOf(saveData, saveData.length);
        this.cart.loadSaveData(this.saveData);
    }

    public byte[] saveSaveData() {
        return this.cart.getSaveData();
    }

    public boolean isSaveCompatible() {
        return cart != null && cart.hasBattery();
    }
}