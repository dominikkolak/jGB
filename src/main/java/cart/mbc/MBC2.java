package cart.mbc;

import cart.constants.CartridgeConstants;
import cart.ram.ExternalMemory;
import cart.rom.ReadOnlyMemory;

public class MBC2 implements MemoryBankController {

    private final ReadOnlyMemory rom;
    private final ExternalMemory ram;
    private final int romBankCount;

    private int romBank;
    private boolean ramEnabled;

    public MBC2(ReadOnlyMemory rom, ExternalMemory ram) {
        this.rom = rom;
        this.ram = ram;
        this.romBankCount = rom.getSize() / CartridgeConstants.ROM_BANK_SIZE;

        reset();
    }

    @Override
    public int getCurrentROMBank() {
        return romBank;
    }

    @Override
    public int getCurrentRAMBank() {
        return 0;
    } // no ram banking

    @Override
    public boolean isRAMEnabled() {
        return ramEnabled;
    }

    @Override
    public boolean accepts(int address) {
        return (address >= CartridgeConstants.ROM_BANK_0_START && address <= CartridgeConstants.ROM_BANK_N_END)
                || (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END);
    }

    @Override
    public void reset() {
        romBank = 1;
        ramEnabled = false;
        ram.reset();
    }

    @Override
    public byte read(int address) {
        // 0000–3FFF : ROM bank 0
        if (address <= CartridgeConstants.ROM_BANK_0_END) {
            return rom.read(address);
        }

        // 4000–7FFF : Switchable ROM bank
        if (address <= CartridgeConstants.ROM_BANK_N_END) {
            int offset = address - CartridgeConstants.ROM_BANK_N_START;
            int physical = romBank * CartridgeConstants.ROM_BANK_SIZE + offset;
            return rom.read(physical);
        }

        // A000–BFFF : Internal RAM
        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {
            if (!ramEnabled) {
                return (byte) 0xFF;
            }
            int reducedAddress = (address - CartridgeConstants.RAM_START) & 0x01FF;
            return ram.read(reducedAddress);
        }

        return (byte) 0xFF;
    }

    // RAM enable = (range A) AND (bit8=0)
    // ROM select = (range B) AND (bit8=1)

    // 0x2EFF = 0010 1110 1111 1111
    // no ram enable from 2000–3FFF! (moon)

    // RAM enable = range 0000–1FFF AND bit8=0
    // ROM bank  = range 2000–3FFF AND bit8=1

    // works well enough but struggles to pass mooneye test
    public void write(int address, int value) {
        // 0000–3FFF : Control registers
        if (address <= 0x3FFF) {
            // bit 8 decides
            if ((address & 0x0100) == 0) {
                ramEnabled = (value & 0x0F) == CartridgeConstants.RAM_ENABLE_VALUE;
                if (ramEnabled) {
                    ram.enable();
                } else {
                    ram.disable();
                }
            } else {
                // lower 4 bits
                romBank = value & 0x0F;
                if (romBank == 0) {
                    romBank = 1;
                }
                romBank %= romBankCount;
            }
            return;
        }

        // A000–BFFF : RAM write
        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {
            if (!ramEnabled) return;

            int ramAddress = (address - CartridgeConstants.RAM_START) & 0x01FF;
            ram.write(ramAddress, value);
        }
    }
}

