package cpu.state;

import shared.Component;

public class InterruptState implements Component {
    private boolean ime; // Interrupt Master Enable
    private boolean imeScheduled; // will be enabled after next instruction

    public InterruptState() {
        reset();
    }

    @Override
    public void tick(int cycles) {
        // scheduled enable
        if (imeScheduled) {
            ime = true;
            imeScheduled = false;
        }
    }

    @Override
    public void reset() {
        ime = false;
        imeScheduled = false;
    }

    @Override
    public String getComponentName() {
        return "Interrupt State";
    }

    public boolean isInterruptMasterEnabled() {
        return ime;
    }

    public void enableInterrupts() {
        imeScheduled = true; // next instruction effect
    }

    public void disableInterrupts() {
        ime = false;
        imeScheduled = false;
    }

    public void setImeImmediate(boolean value) {
        ime = value;
        imeScheduled = false;
    }
}