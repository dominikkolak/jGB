package cart;

import cart.exceptions.InvalidCartridgeException;
import cart.exceptions.UnsupportedCartridgeException;
import cart.header.CartridgeHeader;
import cart.mbc.MBC0;
import cart.mbc.MBC1;
import cart.mbc.MemoryBankController;
import cart.ram.ExternalMemory;
import cart.ram.NRAM;
import cart.ram.SRAM;
import cart.rom.ROM;
import cart.rom.ReadOnlyMemory;
import cart.rtc.NRTC;
import cart.rtc.RTC;
import cart.rtc.RealTimeClock;
import shared.Addressable;
import shared.Component;

public class Cartridge implements Addressable, Component {

    private final CartridgeHeader header;
    private final ReadOnlyMemory rom;
    private final MemoryBankController mbc;
    private final ExternalMemory ram;
    private final RealTimeClock rtc;

    public Cartridge(byte[] romData) {
        this.rom = new ROM(romData);
        this.header = CartridgeHeader.parse(romData);

        if (!header.isHeaderChecksumValid(romData)) {
            throw new InvalidCartridgeException("Header checksum invalid");
        }
        if (!header.isNintendoLogoValid()) {
            throw new InvalidCartridgeException("Logo invalid");
        }

        this.rtc = initRTC(header);
        this.ram = initRAM(header);
        this.mbc = initMBC(header, rom, ram);
    }

    @Override
    public byte read(int address) {
        return mbc.read(address);
    }

    @Override
    public void write(int address, int value) {
        mbc.write(address, value);
    }

    @Override
    public boolean accepts(int address) {
        return mbc.accepts(address);
    }

    @Override
    public void reset() {
        mbc.reset();
        rtc.reset();
    }

    public CartridgeHeader getHeader() { return header; }

    public String getTitle() { return header.title(); }

    public int getCurrentROMBank() { return mbc.getCurrentROMBank(); }

    public int getCurrentRAMBank() { return mbc.getCurrentRAMBank(); }

    public boolean isRAMEnabled() { return mbc.isRAMEnabled(); }

    public boolean hasBattery() { return header.hasBattery(); }

    public boolean hasRTC() { return header.hasRTC(); }

    public byte[] getSaveData() {
        if (!hasBattery()) { return new byte[0]; }
        return ram.getData();
    }

    public void loadSaveData(byte[] saveData) {
        if (!hasBattery() || saveData == null) { return; }
        ram.loadData(saveData);
    }

    private RealTimeClock initRTC(CartridgeHeader header) {
        return header.hasRTC() ? new RTC() : NRTC.INSTANCE;
    }

    private ExternalMemory initRAM(CartridgeHeader header) {
        if (!header.hasRAM()) { return NRAM.INSTANCE; }

        int ramSize = header.ramSize().sizeInBytes;

        if (ramSize == 0) { return NRAM.INSTANCE; }

        return switch (header.cartridgeType()) {
            case ROM_RAM, ROM_RAM_BATTERY,
                 MBC1_RAM, MBC1_RAM_BATTERY,
                 MBC3_RAM, MBC3_RAM_BATTERY,
                 MBC3_TIMER_RAM_BATTERY,
                 MBC5_RAM, MBC5_RAM_BATTERY,
                 MBC5_RUMBLE_RAM, MBC5_RUMBLE_RAM_BATTERY,
                 MMM01_RAM, MMM01_RAM_BATTERY,
                 HUC1_RAM_BATTERY, HUC3 -> new SRAM(ramSize);

            case MBC2, MBC2_BATTERY -> throw new UnsupportedCartridgeException("RAM4");
            case MBC7_SENSOR_RUMBLE_RAM_BATTERY, TAMA5 -> throw new UnsupportedCartridgeException("EEPROM");
            case MBC6 -> throw new UnsupportedCartridgeException("MBC6 Flash RAM");
            case POCKET_CAMERA -> throw new UnsupportedCartridgeException("Pocket Camera RAM");

            case ROM_ONLY, MBC1, MBC3, MBC3_TIMER_BATTERY,
                 MBC5, MBC5_RUMBLE, MMM01 -> NRAM.INSTANCE;

            default -> throw new UnsupportedCartridgeException("Unknown RAM type");
        };
    }

    private MemoryBankController initMBC(CartridgeHeader header, ReadOnlyMemory rom, ExternalMemory ram) {

        if (detectMBC1M(header)) { throw new UnsupportedCartridgeException("MBC1M"); }
        if (detectM161(header)) { throw new UnsupportedCartridgeException("M161"); }

        return switch (header.cartridgeType()) {
            case ROM_ONLY, ROM_RAM, ROM_RAM_BATTERY -> new MBC0(rom, ram);
            case MBC1, MBC1_RAM, MBC1_RAM_BATTERY -> new MBC1(rom, ram);

            case MBC2, MBC2_BATTERY -> throw new UnsupportedCartridgeException("MBC2");
            case MBC3, MBC3_RAM, MBC3_RAM_BATTERY -> throw new UnsupportedCartridgeException("MBC3");
            case MBC3_TIMER_BATTERY, MBC3_TIMER_RAM_BATTERY -> throw new UnsupportedCartridgeException("MBC3 RTC");
            case MBC5, MBC5_RAM, MBC5_RAM_BATTERY,
                 MBC5_RUMBLE, MBC5_RUMBLE_RAM, MBC5_RUMBLE_RAM_BATTERY -> throw new UnsupportedCartridgeException("MBC5");
            case MBC6 -> throw new UnsupportedCartridgeException("MBC6");
            case MBC7_SENSOR_RUMBLE_RAM_BATTERY -> throw new UnsupportedCartridgeException("MBC7");
            case MMM01, MMM01_RAM, MMM01_RAM_BATTERY -> throw new UnsupportedCartridgeException("MMM01");
            case POCKET_CAMERA -> throw new UnsupportedCartridgeException("Pocket Camera");
            case TAMA5 -> throw new UnsupportedCartridgeException("TAMA5");
            case HUC3 -> throw new UnsupportedCartridgeException("HuC3");
            case HUC1_RAM_BATTERY -> throw new UnsupportedCartridgeException("HuC1");
            default -> throw new UnsupportedCartridgeException("Unknown MBC type");
        };
    }

    private boolean detectMBC1M(CartridgeHeader header) {
        // TODO
        return false;
    }

    private boolean detectM161(CartridgeHeader header) {
        // TODO
        return false;
    }
}