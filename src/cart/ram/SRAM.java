package cart.ram;

public class SRAM implements ExternalMemory {

    private final byte[] data;
    private final int size;
    private boolean enabled;

    public SRAM(int sizeInBytes) {
        if (sizeInBytes <= 0) { throw new IllegalArgumentException("SRAM size must be positive"); }
        if ((sizeInBytes & (sizeInBytes - 1)) != 0) { throw new IllegalArgumentException("SRAM size must be power of 2"); }

        this.size = sizeInBytes;
        this.data = new byte[sizeInBytes];
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
    public boolean accepts(int address) {
        return false;
    }

    @Override
    public byte read(int address) {
        if (!enabled) { return (byte) 0xFF; }
        if (address < 0 || address >= size) { return (byte) 0xFF; }
        return data[address];
    }

    @Override
    public void write(int address, int value) {
        if (!enabled) { return; }
        if (address < 0 || address >= size) { return; }
        data[address] = (byte) value;
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

    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = (byte) 0xFF;
        }
    }

    @Override
    public void reset() {
        enabled = false;
        clear();
    }
}
