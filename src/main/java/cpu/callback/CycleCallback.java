package cpu.callback;

@FunctionalInterface
public interface CycleCallback {

    void consumeCycles(int tCycles);

    static CycleCallback none() { return tCycles -> {}; }

    static CycleCounter counter() { return new CycleCounter(); }

    class CycleCounter implements CycleCallback {
        private long totalCycles = 0;

        @Override
        public void consumeCycles(int tCycles) { totalCycles += tCycles; }

        public long getTotalTCycles() { return totalCycles; }
        public long getTotalMCycles() { return totalCycles / 4; }

        public void reset() { totalCycles = 0; }
    }


}
