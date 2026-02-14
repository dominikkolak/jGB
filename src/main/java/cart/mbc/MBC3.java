package cart.mbc;

import cart.constants.CartridgeConstants;
import cart.ram.ExternalMemory;
import cart.rom.ReadOnlyMemory;
import cart.rtc.RealTimeClock;

// basically just mbc1 but simpler bank handling

public class MBC3 implements MemoryBankController {

    private final ReadOnlyMemory rom;
    private final ExternalMemory ram;
    private final RealTimeClock rtc;

    private final int romBankCount;
    private final int ramBankCount;

    private int romBank;
    private int ramBankRTC;
    private boolean ramEnabled;
    private int latchState;

    public MBC3(ReadOnlyMemory rom, ExternalMemory ram, RealTimeClock rtc) {
        this.rom = rom;
        this.ram = ram;
        this.rtc = rtc;

        this.romBankCount = rom.getSize() / CartridgeConstants.ROM_BANK_SIZE;
        this.ramBankCount = Math.max(1, ram.getSize() / CartridgeConstants.RAM_BANK_SIZE);

        reset();
    }

    @Override
    public int getCurrentROMBank() {
        return romBank;
    }

    @Override
    public int getCurrentRAMBank() {
        return ramBankRTC <= 7 ? ramBankRTC : 0;
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
    public void reset() {
        romBank = 1;
        ramBankRTC = 0;
        ramEnabled = false;
        latchState = 0;
        ram.reset();
        rtc.reset();
    }

    @Override
    public byte read(int address) {
        // Bank 0
        if (address <= CartridgeConstants.ROM_BANK_0_END) {
            return rom.read(address);
        }

        // Switchable
        if (address <= CartridgeConstants.ROM_BANK_N_END) {
            int offset = address - CartridgeConstants.ROM_BANK_N_START;
            int physical = (romBank % romBankCount) * CartridgeConstants.ROM_BANK_SIZE + offset;
            return rom.read(physical);
        }

        // RAM / RTC
        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {

            if (!ramEnabled) {
                return (byte) 0xFF;
            }

            // RTC
            if (ramBankRTC >= 0x08 && ramBankRTC <= 0x0C) {
                return (byte) rtc.read(ramBankRTC);
            }

            // RAM
            if (ramBankRTC <= 0x07) {
                int offset = address - CartridgeConstants.RAM_START;
                int bank = ramBankRTC % ramBankCount;
                int physical = bank * CartridgeConstants.RAM_BANK_SIZE + offset;
                return ram.read(physical);
            }
        }

        return (byte) 0xFF;
    }

    @Override
    public void write(int address, int value) {
        // Enable
        if (address <= 0x1FFF) {
            ramEnabled = (value & 0x0F) == CartridgeConstants.RAM_ENABLE_VALUE;
            if (ramEnabled) ram.enable();
            else ram.disable();
            return;
        }

        // 7 bits
        if (address <= 0x3FFF) {
            romBank = value & 0x7F;
            if (romBank == 0) romBank = 1;
            return;
        }

        // Select
        if (address <= 0x5FFF) {
            ramBankRTC = value & 0x0F;
            return;
        }

        // Latch Clock
        if (address <= 0x7FFF) {
            if (latchState == 0 && value == 1) {
                rtc.latch();
            }
            latchState = value & 1;
            return;
        }

        // RAM / RTC
        if (address >= CartridgeConstants.RAM_START && address <= CartridgeConstants.RAM_END) {

            if (!ramEnabled) return;

            // RTC register
            if (ramBankRTC >= 0x08 && ramBankRTC <= 0x0C) {
                rtc.write(ramBankRTC, value);
                return;
            }

            // RAM
            if (ramBankRTC <= 0x07) {
                int offset = address - CartridgeConstants.RAM_START;
                int bank = ramBankRTC % ramBankCount;
                int physical = bank * CartridgeConstants.RAM_BANK_SIZE + offset;
                ram.write(physical, value);
            }
        }
    }
}
