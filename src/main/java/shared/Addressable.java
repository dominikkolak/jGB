package shared;

public interface Addressable extends Readable, Writable {
    boolean accepts(int address);
}
