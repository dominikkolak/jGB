package cart.mbc;

import shared.Addressable;
import shared.Component;

public interface MemoryBankController extends Addressable, Component {

    int getCurrentROMBank();
    int getCurrentRAMBank();
    boolean isRAMEnabled();

}
