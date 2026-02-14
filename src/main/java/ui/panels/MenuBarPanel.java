package ui.panels;

import cart.exceptions.UnsupportedCartridgeException;
import cart.util.CartridgeLoader;
import core.Overlord;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import save.SaveManager;
import ui.dialog.InputSettingsDialog;
import ui.dialog.VideoSettingsDialog;
import ui.input.KeyboardInputSource;

import java.io.File;

public class MenuBarPanel extends HBox {

    private final Label romLabel = new Label("NO ROM LOADED");
    private final Overlord emulator;
    private final KeyboardInputSource keyboardSource;
    private ScreenPanel screenPanel;
    private SaveManager saveManager;

    private MenuItem saveGameItem;
    private MenuItem loadGameItem;

    private File romFile;

    public MenuBarPanel(Overlord emulator, SaveManager saveManager, KeyboardInputSource keyboardSource) {
        this.emulator = emulator;
        this.keyboardSource = keyboardSource;
        this.saveManager = saveManager;
        this.romFile = null;

        setPrefHeight(32);
        setMinHeight(32);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(4, 12, 4, 12));
        setStyle("-fx-background-color: derive(-color-bg-default, -5%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 0 0 1 0;");

        MenuBar menuBar = createMenuBar();
        menuBar.setStyle("-fx-background-color: transparent;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        romLabel.setStyle("-fx-font-size: 10px; " +
                "-fx-font-family: monospace; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: -color-fg-muted;");

        getChildren().addAll(menuBar, spacer, romLabel);
        updateSaveMenuState();
    }

    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();
        bar.setStyle("-fx-background-color: transparent;");

        Menu fileMenu = new Menu("File");

        MenuItem openRom = new MenuItem("Open ROM...");
        openRom.setOnAction(e -> openRomFile());

        MenuItem closeRom = new MenuItem("Close ROM");
        closeRom.setOnAction(e -> closeRom());

        saveGameItem = new MenuItem("Save Game");
        saveGameItem.setOnAction(e -> saveGame());

        loadGameItem = new MenuItem("Load Game");
        loadGameItem.setOnAction(e -> loadGame());


        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(
                openRom,
                closeRom,
                new SeparatorMenuItem(),
                saveGameItem,
                loadGameItem,
                new SeparatorMenuItem(),
                exit
        );


        Menu videoMenu = new Menu("Video");

        MenuItem paletteSettings = new MenuItem("Display Palette...");
        paletteSettings.setOnAction(e -> showPaletteDialog());

        CheckMenuItem frameLimitToggle = new CheckMenuItem("Frame Limiter");
        frameLimitToggle.setSelected(true);
        frameLimitToggle.setOnAction(e ->
                emulator.setFrameLimitEnabled(frameLimitToggle.isSelected())
        );

        videoMenu.getItems().addAll(
                paletteSettings,
                new SeparatorMenuItem(),
                frameLimitToggle
        );

        Menu audioMenu = new Menu("Audio");

        Menu inputMenu = new Menu("Input");

        MenuItem keyboardSettings = new MenuItem("Keyboard Mapping...");
        keyboardSettings.setOnAction(e -> showInputSettings());

        inputMenu.getItems().add(keyboardSettings);

        bar.getMenus().addAll(fileMenu, videoMenu, audioMenu, inputMenu);
        return bar;
    }

    private void openRomFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open ROM");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Game Boy ROM", "*.gb", "*.gbc")
        );

        Stage stage = (Stage) getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                byte[] rom = CartridgeLoader.loadRom(file);

                romFile = file;
                emulator.loadCartridge(rom);

                saveManager.trackGame(file.getName(), file);

                if (emulator.isSaveCompatible() && saveManager.hasSaveFile(file)) {

                    byte[] saveData = saveManager.loadSave(file);
                    emulator.loadSaveData(saveData);
                }

                setRomName(file.getName());
                updateSaveMenuState();

            } catch (Exception ex) {
                setRomName("UNSUPPORTED ROM TYPE");
                throw new UnsupportedCartridgeException("Cartridge not supported");
            }
        }
    }

    private void closeRom() {
        if (!emulator.isPaused()) { emulator.togglePause(); }
        if (romFile != null && emulator.isSaveCompatible()) {
            saveManager.createSave(romFile, emulator.saveSaveData());
        }

        emulator.reset();
        romFile = null;
        setRomName(null);
        updateSaveMenuState();
    }

    public void setScreenPanel(ScreenPanel panel) {
        this.screenPanel = panel;
    }

    private void showInputSettings() {
        Stage owner = (Stage) getScene().getWindow();
        InputSettingsDialog dialog = new InputSettingsDialog(owner, keyboardSource);
        dialog.showAndWait();
    }

    private void showPaletteDialog() {
        if (screenPanel == null) {
            throw new IllegalStateException("Screen not Initialized");
        }

        Stage owner = (Stage) getScene().getWindow();
        VideoSettingsDialog dialog = new VideoSettingsDialog(owner, screenPanel);
        dialog.showAndWait();
    }

    public void setRomName(String name) {
        romLabel.setText(name == null ? "NO ROM LOADED" : name.toUpperCase());
    }

    private void saveGame() {
        if (romFile == null) return;

        if (!emulator.isSaveCompatible()) return;

        byte[] saveData = emulator.saveSaveData();
        saveManager.createSave(romFile, saveData);
        updateSaveMenuState();
    }

    private void loadGame() {
        if (romFile == null || !emulator.isSaveCompatible()) return;

        byte[] saveData = saveManager.loadSave(romFile);
        if (saveData != null) {
            emulator.loadSaveData(saveData);
        }
        updateSaveMenuState();
    }

    private void updateSaveMenuState() {
        boolean canSave = romFile != null && emulator.isSaveCompatible();

        saveGameItem.setDisable(!canSave);

        boolean canLoad = canSave && saveManager.hasSaveFile(romFile);
        loadGameItem.setDisable(!canLoad);
    }

}