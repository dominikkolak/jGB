package cpu.cpu.interrupt;

import cpu.constants.CPUConstants;
import cpu.interrupt.InterruptProvider;
import cpu.interrupt.InterruptRequester;
import cpu.register.enums.INTERRUPT;
import shared.Addressable;
import shared.Component;
import snapshot.InterruptSnapshot;

public class InterruptController implements InterruptProvider, InterruptRequester, Addressable, Component {

    private boolean ime;
    private boolean imeScheduled;

    private int iEnable;
    private int iFlag;

    public InterruptController() { reset(); }

    @Override
    public boolean isIMEnabled() {
        return ime;
    }

    @Override
    public boolean hasPending() {
        return (iEnable & iFlag & CPUConstants.INTERRUPT_MASK) != 0;
    }

    @Override
    public boolean shouldDispatch() {
        return ime && hasPending();
    }

    @Override
    public INTERRUPT acknowledgeInterrupt() {
        int pending = iEnable & iFlag & CPUConstants.INTERRUPT_MASK;
        if (pending == 0) { return null; }

        int bit = Integer.numberOfTrailingZeros(pending);
        INTERRUPT interrupt = INTERRUPT.fromBit(bit);

        iFlag &= ~interrupt.mask();
        ime = false;
        imeScheduled = false;

        return interrupt;
    }

    @Override
    public void updateIME() {
        if (imeScheduled) {
            ime = true;
            imeScheduled = false;
        }
    }

    @Override
    public void request(INTERRUPT interrupt) {
        iFlag |= interrupt.mask();
    }

    @Override
    public void clear(INTERRUPT interrupt) {
        iFlag &= ~interrupt.mask();
    }

    @Override
    public boolean accepts(int address) {
        return address == CPUConstants.IF_ADDRESS || address == CPUConstants.IE_ADDRESS;
    }

    @Override
    public void reset() {
        ime = false;
        imeScheduled = false;
        iEnable = 0x00;
        iFlag = 0xE1;
    }

    @Override
    public byte read(int address) {
        if (address == CPUConstants.IF_ADDRESS) {
            return (byte) getIF();
        }
        if (address == CPUConstants.IE_ADDRESS) {
            return (byte) getIE();
        }
        return (byte) 0xFF;
    }

    @Override
    public void write(int address, int value) {
        if (address == CPUConstants.IF_ADDRESS) {
            setIF(value);
        }
        if (address == CPUConstants.IE_ADDRESS) {
            setIE(value);
        }
    }

    public void scheduleEnableIME() {
        imeScheduled = true;
    }

    public void disableIME() {
        ime = false;
        imeScheduled = false;
    }

    public void enableIME() {
        ime = true;
        imeScheduled = false;
    }

    public int getIE() {
        return iEnable;
    }

    public int getIF() {
        return iFlag | CPUConstants.IF_UNUSED_BITS;
    }

    public void setIE(int value) {
        iEnable = value & CPUConstants.INTERRUPT_MASK;
    }

    public void setIF(int value) {
        iFlag = (value & CPUConstants.INTERRUPT_MASK) | CPUConstants.IF_UNUSED_BITS;
    }

    public boolean isIMEScheduled() {
        return imeScheduled;
    }

    public InterruptSnapshot createSnapshot() {
        return new InterruptSnapshot(
                isIMEnabled(),
                getIE() & 0xFF,
                getIF() & 0xFF
        );
    }
}
