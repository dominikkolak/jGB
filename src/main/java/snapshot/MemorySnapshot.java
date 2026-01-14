package snapshot;

import java.util.Map;

public record MemorySnapshot(Map<Integer, Integer> stackWindow,
                             Map<Integer, Integer> codeWindow,
                             Map<Integer, Integer> customWindow
) {}
