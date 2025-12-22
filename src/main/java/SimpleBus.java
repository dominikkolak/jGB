import cpu.interfaces.BUS;

public class SimpleBus implements BUS {
    private final byte[] memory;

    public SimpleBus(int size) {
        this.memory = new byte[size];
    }

    @Override
    public int readByte(int address) {
        address &= 0xFFFF; // Wrap to 16-bit
        if (address >= memory.length) {
            return 0xFF; // 0xFF unmapped memory
        }
        return memory[address] & 0xFF;
    }

    @Override
    public void writeByte(int address, int value) {
        address &= 0xFFFF;
        if (address < memory.length) {
            memory[address] = (byte) (value & 0xFF);
        }
    }

    public void loadProgram(int address, byte[] program) {
        System.arraycopy(program, 0, memory, address, program.length);
    }
}