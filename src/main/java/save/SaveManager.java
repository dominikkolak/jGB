package save;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaveManager {

    private static final File BASE_DIR = resolveBaseDirectory();
    private static final File SAVES_DIR = new File(BASE_DIR, "saves");
    private static final File HISTORY_FILE = new File(BASE_DIR, "game_history.dat");

    private List<GameHistory> history = new ArrayList<>();

    public SaveManager() {
        SAVES_DIR.mkdirs();
        loadHistory();
    }


    public void loadHistory() {
        File file = HISTORY_FILE;
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            history = (List<GameHistory>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            history = new ArrayList<>();
            throw new RuntimeException("Failed to Load History");
        }
    }

    private void saveHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(history);
        } catch (IOException e) {
            throw new RuntimeException("Failed to Save History");
        }
    }

    public boolean checkTrackedGames(String name, File path) {
        return history.stream()
                .anyMatch(entry -> entry.getTitle().equals(name) &&
                        entry.getPath().equals(path.getAbsolutePath()));
    }

    public void trackGame(String name, File path) {
        Optional<GameHistory> existing = history.stream()
                .filter(entry -> entry.getPath().equals(path.getAbsolutePath()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().updateLastPlayed();
        } else {
            history.add(new GameHistory(name, path.getAbsolutePath()));
        }
        saveHistory();
    }

    private File getSaveFile(File romPath) {
        String romName = romPath.getName();
        String saveName = romName.substring(0, romName.lastIndexOf('.')) + ".sav";
        return new File(SAVES_DIR, saveName);
    }

    public byte[] loadSave(File romPath) {
        File saveFile = getSaveFile(romPath);

        if (!saveFile.exists()) {
            return null;
        }

        try {
            return Files.readAllBytes(saveFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to Load Save");
        }
    }

    public boolean createSave(File romPath, byte[] saveData) {
        File saveFile = getSaveFile(romPath);

        try {
            Files.write(saveFile.toPath(), saveData);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to Create Save");
        }
    }

    public boolean hasSaveFile(File romPath) {
        return getSaveFile(romPath).exists();
    }

    public boolean deleteSave(File romPath) {
        File saveFile = getSaveFile(romPath);
        return saveFile.exists() && saveFile.delete();
    }

    public List<GameHistory> getRecentGames(int limit) {
        return history.stream()
                .sorted((a, b) -> Long.compare(b.getLastPlayed(), a.getLastPlayed()))
                .limit(limit)
                .toList();
    }

    public List<GameHistory> getAllGames() {
        return new ArrayList<>(history);
    }

    private static File resolveBaseDirectory() {
        try {
            File appDir = new File(
                    SaveManager.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            ).getParentFile();

            File portableDir = new File(appDir, "data");

            if (portableDir.exists() || portableDir.mkdirs()) {
                if (portableDir.canWrite()) {
                    return portableDir;
                }
            }
        } catch (Exception ignored) {}

        return new File(System.getProperty("user.home"), ".gbemu");
    }

}
