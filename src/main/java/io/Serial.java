package io;

import mem.MemoryConstants;
import shared.Addressable;
import shared.Component;

public class Serial implements Addressable, Component {

    private int sb;
    private int sc;

    private final StringBuilder output = new StringBuilder();
    private SerialOutputListener outputListener;

    public void setOutputListener(SerialOutputListener listener) {
        this.outputListener = listener;
    }


    @Override
    public boolean accepts(int address) {
        return address == MemoryConstants.SB || address == MemoryConstants.SC;
    }

    @Override
    public void reset() {
        sb = 0x00;
        sc = 0x7E;
        output.setLength(0);
    }

    @Override
    public byte read(int address) {
        return switch (address) {
            case MemoryConstants.SB -> (byte) sb;
            case MemoryConstants.SC -> (byte) (sc | 0x7E);
            default -> (byte) 0xFF;
        };
    }

    @Override
    public void write(int address, int value) {
        value &= 0xFF;

        switch (address) {
            case MemoryConstants.SB -> sb = value;
            case MemoryConstants.SC -> {
                sc = value;

                if ((sc & 0x81) == 0x81) {
                    char c = (char) sb;
                    output.append(c);

                    if (outputListener != null) {
                        outputListener.onSerialOutput(c);
                    }

                    sc &= 0x7F;
                    sb = 0xFF;
                }
            }
        }
    }

    public String getOutput() {
        return output.toString();
    }

    public void clearOutput() {
        output.setLength(0);
    }

    public boolean hasOutput() {
        return !output.isEmpty();
    }

}
