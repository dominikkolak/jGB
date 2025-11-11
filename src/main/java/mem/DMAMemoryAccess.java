package mem;

@FunctionalInterface
public interface DMAMemoryAccess {

    byte read(int address);

}
