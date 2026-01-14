import cart.util.CartridgeLoader;
import core.Overlord;
import ui.UserInterface;
import java.io.IOException;
import java.util.Arrays;

public class Emulator {

    public static void main(String[] args) throws IllegalArgumentException, IOException {

        boolean headless = Arrays.asList(args).contains("--headless");

        if (headless) {
            runHeadless(args);
        } else {
            runNormal(args);
        }

    }

    private static void runHeadless(String[] args) throws IOException {
        System.out.println("Running in Headless mode");
        Overlord emu = new Overlord();
        Thread emuThread = new Thread(emu);
        byte[] rom = CartridgeLoader.loadRom("/home/dom/XXX/JGBE/rom/gb-test-roms-master/interrupt_time/interrupt_time.gb");
        emu.loadCartridge(rom);
        emuThread.start();
    }

    private static void runNormal(String[] args) throws IOException {
        System.out.println("Running in Normal mode");
        UserInterface.main(args);
    }

}
