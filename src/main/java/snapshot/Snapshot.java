package snapshot;

public record Snapshot(
        RegisterSnapshot registers,
        FlagSnapshot flags,
        InstructionSnapshot instructions,
        MemorySnapshot memory,
        InterruptSnapshot interrupts,
        PictureRegisterSnapshot ppu
) {}
