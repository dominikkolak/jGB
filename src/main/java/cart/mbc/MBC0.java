package cart.mbc;

import cart.constants.CartridgeConstants;
import cart.ram.ExternalMemory;
import cart.rom.ReadOnlyMemory;

public class MBC0 implements MemoryBankController {

    private final ReadOnlyMemory rom;
    private final ExternalMemory ram;

    public MBC0(ReadOnlyMemory rom, ExternalMemory ram) {
        this.rom = rom;
        this.ram = ram;

        ram.enable(); // MBC0 no RAM enabled register
    }

    @Override
    public int getCurrentROMBank() { return 0; }

    @Override
    public int getCurrentRAMBank() { return 0; }

    @Override
    public boolean isRAMEnabled() { return ram.isEnabled(); }

    @Override
    public boolean accepts(int address) {
        return (address >= CartridgeConstants.ROM_BANK_0_START && address <= CartridgeConstants.ROM_BANK_N_END)
                || (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END);
    }

    @Override
    public byte read(int address) {
        if (address <= CartridgeConstants.ROM_BANK_N_END) {
            return rom.read(address);
        }

        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {
            return ram.read(address - CartridgeConstants.RAM_START);
        }
        return (byte) 0xFF;
    }

    @Override
    public void write(int address, int value) {
        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {
            ram.write(address - CartridgeConstants.RAM_START, value);
        }
    }

    @Override
    public void reset() {
        ram.reset();
        ram.enable();
    }
}
