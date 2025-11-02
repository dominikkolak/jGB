package mtc;

import shared.Clocked;
import shared.Component;

import java.util.ArrayList;
import java.util.List;

public class MasterTimeController implements Component {

    private long totalCycles;
    private int frameCycles;
    private long frameCount;

    private final List<Clocked> components = new ArrayList<>();

    private boolean frameLimiter = true;

    private long frameStart;

    private double currentFPS;
    private long lastFPSUpdate;
    private int framesSinceLastUpdate;

    public MasterTimeController() {
        reset();
    }

    public void registerComponent(Clocked component) {
        components.add(component);
    }

    public void deregisterComponent(Clocked component) {
        components.remove(component);
    }

    public void addCycles(int cycles) {
        totalCycles += cycles;
        frameCycles += cycles;

        for (int i = 0; i < components.size(); i++) {
            components.get(i).tick(cycles);
        }
    }

    public void startFrame() {
        frameCycles = 0;
        frameStart = System.nanoTime();
    }

    public boolean isFrameComplete() {
        return frameCycles >= TimingConstants.CYCLES_PER_FRAME;
    }

    public void endFrame() {
        frameCount++;
        framesSinceLastUpdate++;

        updateFPS();

        if (frameLimiter) {
            waitForFrameEnd();
        }
    }

    public void waitForFrameEnd() {
        long targetEndTime = frameStart + TimingConstants.FRAME_TIME_NANOS;
        long now = System.nanoTime();
        long remaining = targetEndTime - now;

        if (remaining > 2000000) {
            try {
                Thread.sleep((remaining - 1000000) / 1000000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        while (System.nanoTime() < targetEndTime) {
            Thread.onSpinWait();
        }
    }

    private void updateFPS() {
        long now = System.nanoTime();
        long elapsed = now - lastFPSUpdate;

        if (elapsed >= 1000000000) {
            currentFPS = framesSinceLastUpdate * 1000000000.0 / elapsed;
            framesSinceLastUpdate = 0;
            lastFPSUpdate = now;
        }
    }

    @Override
    public void reset() {
        totalCycles = 0;
        frameCycles = 0;
        frameCount = 0;
        currentFPS = 0;
        lastFPSUpdate = System.nanoTime();
        framesSinceLastUpdate = 0;
        frameStart = System.nanoTime();
    }

    public long getTotalCycles() { return totalCycles; }
    public long getTotalMCycles() { return totalCycles / 4; }
    public int getFrameCycles() { return frameCycles; }
    public int getCurrentScanline() { return frameCycles / TimingConstants.CYCLES_PER_SCANLINE; }
    public long getFrameCount() { return frameCount; }
    public double getCurrentFps() { return currentFPS; }
    public boolean isFrameLimitEnabled() { return frameLimiter; }

    public void setFrameLimitEnabled(boolean enabled) { this.frameLimiter = enabled; }


}
