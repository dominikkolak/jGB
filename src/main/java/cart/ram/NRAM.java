package cart.ram;

public class NRAM implements ExternalMemory {

    public static final NRAM INSTANCE = new NRAM();

    private NRAM() {}

    @Override
    public byte read(int address) { return (byte) 0xFF; }

    @Override
    public void write(int address, int value) {}

    @Override
    public int getSize() { return 0; }

    @Override
    public void enable() {}

    @Override
    public void disable() {}

    @Override
    public boolean isEnabled() { return false; }

    @Override
    public byte[] getData() { return new byte[0]; }

    @Override
    public void loadData(byte[] data) {}

    @Override
    public boolean accepts(int address) { return false; }

    @Override
    public void reset() {}
}
