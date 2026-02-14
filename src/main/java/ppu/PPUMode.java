package ppu;

public enum PPUMode {

    HBLANK(0, 204),
    VBLANK(1, 456),
    OAM_SCAN(2, 80),
    DRAWING(3, 172);

    private final int flag;
    private final int baseDuration;

    PPUMode(int flag, int baseDuration) {
        this.flag = flag;
        this.baseDuration = baseDuration;
    }

    public int flag() { return flag; }

    public int baseDuration() { return baseDuration; }

    public boolean VRAMAccessible() {
        return this == HBLANK || this == VBLANK;
    }

    public boolean OAMAccessible() {
        return this == HBLANK || this == VBLANK;
    }

}
