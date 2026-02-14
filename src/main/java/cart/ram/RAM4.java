package cart.ram;

// for mbc2, half bytes

public class RAM4 implements ExternalMemory {

    private final byte[] data;
    private final int size;
    private boolean enabled;

    public RAM4() {
        this.size = 512;
        this.data = new byte[size];
        this.enabled = false;

        clear();
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public byte[] getData() {
        byte[] copy = new byte[size];
        System.arraycopy(data, 0, copy, 0, size);
        return copy;
    }

    @Override
    public void loadData(byte[] saveData) {
        if (saveData == null) { throw new IllegalArgumentException("Save data null"); }
        int length = Math.min(saveData.length, size);
        System.arraycopy(saveData, 0, data, 0, length);
    }

    @Override
    public boolean accepts(int address) {
        return false;
    }

    @Override
    public void reset() {
        enabled = false;
        clear();
    }

    @Override
    public byte read(int address) {
        if (!enabled) { return (byte) 0xFF; }
        int index = address & 0x01FF;
        return (byte) (data[index] | 0xF0);
    }

    @Override
    public void write(int address, int value) {
        if (!enabled) { return; }
        int index = address & 0x01FF;
        data[index] = (byte) (value & 0x0F);
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = (byte) 0x0F;
        }
    }
}
