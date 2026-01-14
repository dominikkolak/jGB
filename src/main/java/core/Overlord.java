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

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Overlord implements Runnable {

    private Core emulator;
    private volatile boolean running = true;

    private Cartridge cart;

    private final int[] primaryBuffer = new int[160 * 144];
    private final int[] secondaryBuffer = new int[160 * 144];
    private volatile int[] frameBuffer = primaryBuffer;

    private final BlockingDeque<Snapshot> snapshotQueue = new LinkedBlockingDeque<>();
    private volatile boolean debugMode = false;
    private volatile int snapshotInterval = 1;
    private int framesSinceSnapshot = 0;

    public Overlord() {
        this.emulator = new Core();
        this.cart = null;
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

                if (debugMode) {
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

    public void loadCartridge(byte[] rom) {
        cart = new Cartridge(rom);
        emulator.loadCartridge(cart);
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
    }

    public boolean isCartridgeLoaded() {
        return emulator.isCartridgeLoaded();
    }

    public void togglePause() {
        emulator.togglePause();
    }

    public void setInputProvider(InputProvider input) {
        emulator.setInputProvider(input);
    }

    private void createSnapshot() {
        Snapshot snapshot = emulator.createSnapshot();
        snapshotQueue.offer(snapshot); // non blocking
    }

    public Snapshot pollSnapshot() {
        return snapshotQueue.poll();
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
        if (!enabled) {
            snapshotQueue.clear();
        }
    }

    public void setSnapshotInterval(int frames) {
        this.snapshotInterval = Math.max(1, frames);
    }

    public void forceSnapshot() {
        if (emulator.isCartridgeLoaded()) {
            createSnapshot();
        }
    }

    public void setSnapshotRange(int start, int end) {
        emulator.setSnapshotRange(start, end);
    }

}
