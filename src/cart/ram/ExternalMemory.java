package cart.ram;

import shared.Addressable;
import shared.Component;

public interface ExternalMemory extends Addressable, Component {

    int getSize();

    void enable();
    void disable();

    boolean isEnabled();

    byte[] getData();
    void loadData(byte[] data);

}
