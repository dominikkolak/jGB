package mem;

import cpu.interrupt.InterruptController;
import cpu.interrupt.Timer;
import io.Serial;
import ppu.PPU;
import shared.Addressable;
import shared.Component;

public class IO implements Addressable, Component {

    private final byte[] data = new byte[MemoryConstants.IO_SIZE];

    private InterruptController interrupts;
    private Timer timer;
    private PPU ppu;
    private Addressable apu;
    private Addressable joypad;
    private Serial serial;

    @Override
    public boolean accepts(int address) {
        return address >= MemoryConstants.IO_START && address < MemoryConstants.IO_END;
    }

    @Override
    public void reset() {
        java.util.Arrays.fill(data, (byte) 0);
        data[MemoryConstants.P1_JOYP - MemoryConstants.IO_START] = (byte) 0xCF;
    }

    @Override
    public byte read(int address) {
        switch (address) {
            case MemoryConstants.P1_JOYP: return joypad != null ? joypad.read(address) : (byte) 0xCF;
            case MemoryConstants.SB:
            case MemoryConstants.SC: return serial != null ? serial.read(address) : data[address - MemoryConstants.IO_START];
            case MemoryConstants.DIV:
            case MemoryConstants.TIMA:
            case MemoryConstants.TMA:
            case MemoryConstants.TAC: return timer != null ? timer.read(address) : data[address - MemoryConstants.IO_START];
            case MemoryConstants.IF: return interrupts != null ? (byte) interrupts.getIF() : data[address - MemoryConstants.IO_START];
            case MemoryConstants.LCDC:
            case MemoryConstants.STAT:
            case MemoryConstants.SCY:
            case MemoryConstants.SCX:
            case MemoryConstants.LY:
            case MemoryConstants.LYC:
            case MemoryConstants.BGP:
            case MemoryConstants.OBP0:
            case MemoryConstants.OBP1:
            case MemoryConstants.WY:
            case MemoryConstants.WX: return ppu != null ? ppu.read(address) : data[address - MemoryConstants.IO_START];

            case MemoryConstants.DMA: return data[address - MemoryConstants.IO_START]; // mmu fallback

            default:
                if (address >= MemoryConstants.APU_START && address <= MemoryConstants.APU_END) {
                    return apu != null ? apu.read(address) : data[address - MemoryConstants.IO_START];
                }
                return data[address - MemoryConstants.IO_START];
        }
    }

    @Override
    public void write(int address, int value) {
        switch (address) {
            case MemoryConstants.P1_JOYP:
                if (joypad != null) joypad.write(address, value);
                else data[address - MemoryConstants.IO_START] = (byte) value;
                break;
            case MemoryConstants.SB:
            case MemoryConstants.SC:
                if (serial != null) serial.write(address, value);
                else data[address - MemoryConstants.IO_START] = (byte) value;
                break;
            case MemoryConstants.DIV:
            case MemoryConstants.TIMA:
            case MemoryConstants.TMA:
            case MemoryConstants.TAC:
                if (timer != null) timer.write(address, value);
                else data[address - MemoryConstants.IO_START] = (byte) value;
                break;
            case MemoryConstants.IF:
                if (interrupts != null) interrupts.setIF(value);
                else data[address - MemoryConstants.IO_START] = (byte) value;
                break;
            case MemoryConstants.LCDC:
            case MemoryConstants.STAT:
            case MemoryConstants.SCY:
            case MemoryConstants.SCX:
            case MemoryConstants.LY:
            case MemoryConstants.LYC:
            case MemoryConstants.BGP:
            case MemoryConstants.OBP0:
            case MemoryConstants.OBP1:
            case MemoryConstants.WY:
            case MemoryConstants.WX:
                if (ppu != null) ppu.write(address, value);
                else data[address - MemoryConstants.IO_START] = (byte) value;
                break;

            case MemoryConstants.DMA: // mmu fallback
                data[address - MemoryConstants.IO_START] = (byte) value;
                break;

            default:
                if (address >= MemoryConstants.APU_START && address <= MemoryConstants.APU_END) {
                    if (apu != null) apu.write(address, value);
                    else data[address - MemoryConstants.IO_START] = (byte) value;
                } else {
                    data[address - MemoryConstants.IO_START] = (byte) value;
                }
                break;
        }
    }

    public void setInterrupts(InterruptController ic) { this.interrupts = ic; }
    public void setTimer(Addressable timer) { this.timer = (Timer) timer; }
    public void setPPU(Addressable ppu) { this.ppu = (PPU) ppu; }
    public void setAPU(Addressable apu) { this.apu = apu; }
    public void setJoypad(Addressable joypad) { this.joypad = joypad; }
    public void setSerial(Serial serial) { this.serial = serial; }
}