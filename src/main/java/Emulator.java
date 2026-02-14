import cart.util.CartridgeLoader;
import core.Overlord;
import save.SaveManager;
import ui.UserInterface;
import ui.backup.BackupUserInterface;
import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.util.Arrays;

public class Emulator {

    public static void main(String[] args) throws IllegalArgumentException, IOException {

        boolean headless = Arrays.asList(args).contains("--headless");
        boolean backup = Arrays.asList(args).contains("--backup");

        if (headless) {
            runHeadless(args);
        } else {
            if (backup) { runBackup(args); }
            runNormal(args);
        }
    }

    private static void runHeadless(String[] args) throws IOException {
        System.out.println("Running in Headless Mode");
        Overlord emu = new Overlord();
        Thread emuThread = new Thread(emu);
        emuThread.start();
    }

    private static void runNormal(String[] args) throws IOException {
        System.out.println("Running in Normal Mode");
        UserInterface.main(args);
    }

    private static void runBackup(String[] args) throws IOException {
        System.out.println("Running in Backup Mode");
        BackupUserInterface.main(args);
    }

}
