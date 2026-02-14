package cart.mbc;

import cart.constants.CartridgeConstants;
import cart.ram.ExternalMemory;
import cart.rom.ReadOnlyMemory;

public class MBC1 implements MemoryBankController {

    private final ReadOnlyMemory rom;
    private final ExternalMemory ram;
    private final int romBankCount;
    private final int ramBankCount;

    private int romBank;
    private int ramBank;
    private boolean ramEnabled;
    private boolean bankMode;  // false = ROM mode, true = RAM mode

    public MBC1(ReadOnlyMemory rom, ExternalMemory ram) {
        this.rom = rom;
        this.ram = ram;
        this.romBankCount = rom.getSize() / CartridgeConstants.ROM_BANK_SIZE;
        this.ramBankCount = Math.max(1, ram.getSize() / CartridgeConstants.RAM_BANK_SIZE);

        reset();
    }

    @Override
    public int getCurrentROMBank() {
        return calculateRomBank();
    }

    @Override
    public int getCurrentRAMBank() {
        return bankMode ? ramBank : 0;
    }

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
    public byte read(int address) {
        // Bank 0: 0x0000-0x3FFF
        if (address <= CartridgeConstants.ROM_BANK_0_END) {
            int bank = (bankMode && romBankCount >= 64) ? ((ramBank << 5) % romBankCount) : 0;
            int physical = (bank * CartridgeConstants.ROM_BANK_SIZE) + address;
            return rom.read(physical);
        }
        // Bank N: 0x4000-0x7FFF
        if (address <= CartridgeConstants.ROM_BANK_N_END) {
            int bank = calculateRomBank();
            int offset = address - CartridgeConstants.ROM_BANK_N_START;
            int physical = (bank * CartridgeConstants.ROM_BANK_SIZE) + offset;
            return rom.read(physical);
        }
        // RAM: 0xA000-0xBFFF
        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {
            if (!ramEnabled) { return (byte) 0xFF; }
            return ram.read(calculateRamAddress(address));
        }
        return (byte) 0xFF;
    }

    @Override
    public void write(int address, int value) {
        // 0x0000-0x1FFF | RAMG | RAM Enable (0x0A) X
        if (address <= 0x1FFF) {
            ramEnabled = (value & 0x0F) == CartridgeConstants.RAM_ENABLE_VALUE;
            if (ramEnabled) { ram.enable(); }
            else { ram.disable(); }
            return;
        }
        // 0x2000-0x3FFF | BANK1 | ROM bank lower 5 bits
        if (address <= CartridgeConstants.ROM_BANK_0_END) {
            romBank = value & 0x1F; // Always store the raw 5-bit value, 256 quirk
            return;
        }
        // 0x4000-0x5FFF | BANK2 | RAM bank OR ROM upper 2 bits
        if (address <= 0x5FFF) {
            ramBank = value & 0x03;
            return;
        }
        // 0x6000-0x7FFF | MODE | Banking mode select
        if (address <= CartridgeConstants.ROM_BANK_N_END) {
            bankMode = (value & 0x01) == 1;
            return;
        }
        // 0xA000-0xBFFF: RAM write
        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {
            if (!ramEnabled) { return; }
            ram.write(calculateRamAddress(address), value);
        }
    }

    private int calculateRomBank() {
        int bank = romBank | (ramBank << 5);
        // Banks 0x00, 0x20, 0x40, 0x60
        if ((bank & 0x1F) == 0) { bank++; }
        return bank % romBankCount;
    }

    private int calculateRamAddress(int address) {
        int offset = address - CartridgeConstants.RAM_START;
        int ramSize = ram.getSize();

        if (ramSize <= 8 * 1024) {
            if (ramSize == 0) {return 0xFF;}
            return offset % ramSize;
        }

        int bank = bankMode ? ramBank : 0;
        return (bank * CartridgeConstants.RAM_BANK_SIZE) + offset;
    }

    @Override
    public void reset() {
        romBank = 0; // 0x00 -> 1
        ramBank = 0;
        ramEnabled = false;
        bankMode = false;
        ram.reset();
    }

}
