package snapshot;

public record InterruptSnapshot(boolean imeEnabled,
                                int ieRegister,
                                int ifRegister
) {}
