package ui;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import core.Overlord;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import save.SaveManager;
import ui.demo.StubPanel;
import ui.input.FocusManager;
import ui.input.InputManager;
import ui.input.KeyboardInputSource;
import ui.panels.*;

import javafx.application.Platform;

public class UserInterface extends Application {

    private Overlord overlord;
    private InputManager inputManager;
    private KeyboardInputSource keyboardSource;
    private FocusManager focusManager;

    private SaveManager saveManager;

    private RegisterPanel registerPanel;
    private InstructionPanel instructionPanel;
    private MemoryPanel memoryPanel;
    private PicturePanel ppuPanel;
    private FlagInterruptPanel flagsInterruptsPanel;
    private ScreenPanel screenPanel;
    private TerminalPanel terminalPanel;

    @Override
    public void start(Stage primaryStage) {
        keyboardSource = new KeyboardInputSource();
        inputManager = new InputManager();
        inputManager.addSource(keyboardSource);
        saveManager = new SaveManager();

        overlord = new Overlord();
        overlord.setInputProvider(inputManager);

        Thread emulatorThread = new Thread(overlord, "Emulator-Thread");
        emulatorThread.setDaemon(true);
        emulatorThread.start();

        MenuBarPanel menuBar = new MenuBarPanel(overlord, saveManager, keyboardSource);
        StatusBarPanel statusBar = new StatusBarPanel(overlord);

        HBox debugToolbar = createDebugToolbar();

        boolean dark = Platform.getPreferences().getColorScheme() ==
                javafx.application.ColorScheme.DARK;

        if (dark) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setHgap(5);
        grid.setVgap(5);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(250);
        col0.setPrefWidth(300);
        col0.setMaxWidth(400);
        col0.setHgrow(Priority.NEVER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(250);
        col2.setPrefWidth(300);
        col2.setMaxWidth(400);
        col2.setHgrow(Priority.NEVER);

        grid.getColumnConstraints().addAll(col0, col1, col2);

        RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);

        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        row1.setMinHeight(150);
        row1.setPrefHeight(200);
        row1.setMaxHeight(300);

        grid.getRowConstraints().addAll(row0, row1);

        registerPanel = new RegisterPanel();
        instructionPanel = new InstructionPanel();
        flagsInterruptsPanel = new FlagInterruptPanel();

        screenPanel = new ScreenPanel(overlord);
        terminalPanel = new TerminalPanel();

        overlord.getCore().setSerialOutputListener(terminalPanel::handleSerialOutput);

        ppuPanel = new PicturePanel();
        memoryPanel = new MemoryPanel();
        StubPanel audioPanel = new StubPanel("WIP AUDIO");

        menuBar.setScreenPanel(screenPanel);

        setupCallbacks(instructionPanel, memoryPanel);

        startSnapshotUpdater();

        overlord.setSnapshotRange(0x0000, 0x003F);

        VBox leftColumn = new VBox(5);
        leftColumn.getChildren().addAll(registerPanel, instructionPanel);

        registerPanel.setPrefHeight(180);
        instructionPanel.setPrefHeight(300);
        VBox.setVgrow(instructionPanel, Priority.ALWAYS);

        VBox centerColumn = new VBox(5);
        centerColumn.getChildren().add(screenPanel);

        screenPanel.setPrefHeight(500);
        VBox.setVgrow(screenPanel, Priority.ALWAYS);

        VBox rightColumn = new VBox(5);
        rightColumn.getChildren().addAll(ppuPanel, memoryPanel);

        ppuPanel.setPrefHeight(180);
        memoryPanel.setPrefHeight(300);
        VBox.setVgrow(memoryPanel, Priority.ALWAYS);

        grid.add(leftColumn, 0, 0);
        grid.add(centerColumn, 1, 0);
        grid.add(rightColumn, 2, 0);

        grid.add(flagsInterruptsPanel, 0, 1);
        grid.add(terminalPanel, 1, 1);
        grid.add(audioPanel, 2, 1);

        BorderPane root = new BorderPane();

        VBox topBar = new VBox(menuBar, debugToolbar);
        root.setTop(topBar);

        root.setCenter(grid);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setTitle("Game Boy Emulator");
        primaryStage.setScene(scene);

        focusManager = new FocusManager(scene, keyboardSource);

        primaryStage.setOnCloseRequest(e -> {
            screenPanel.stop();
            terminalPanel.restore();
            System.exit(0);
        });
        primaryStage.show();

    }

    private HBox createDebugToolbar() {
        HBox toolbar = new HBox(8);
        toolbar.setPadding(new Insets(4, 8, 4, 8));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-background-color: derive(-color-bg-default, -8%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 0 0 1 0;");

        CheckBox debugModeToggle = new CheckBox("DEBUG MODE");
        debugModeToggle.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-font-weight: bold;");
        debugModeToggle.setSelected(true);
        overlord.setDebugMode(true);
        debugModeToggle.setOnAction(e -> {
            boolean enabled = debugModeToggle.isSelected();
            overlord.setDebugMode(enabled);
            if (enabled && overlord.isPaused()) {
                overlord.forceSnapshot();
            }
        });

        Region separator1 = new Region();
        separator1.setPrefWidth(20);

        Button pauseButton = new Button("PAUSE");
        pauseButton.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        pauseButton.setOnAction(e -> {
            overlord.togglePause();
            pauseButton.setText(overlord.isPaused() ? "RESUME" : "PAUSE");
            if (overlord.isPaused()) {
                overlord.forceSnapshot();
            }
        });

        Button stepButton = new Button("STEP");
        stepButton.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        stepButton.setOnAction(e -> {
            if (overlord.isPaused()) {
                overlord.step();
            }
        });

        Button resetButton = new Button("RESET");
        resetButton.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        resetButton.setOnAction(e -> {
            overlord.reset();
            pauseButton.setText("PAUSE");
            if (debugModeToggle.isSelected()) {
                overlord.forceSnapshot();
            }
        });

        Region separator2 = new Region();
        separator2.setPrefWidth(20);

        Label snapshotLabel = new Label("SNAPSHOT EVERY:");
        snapshotLabel.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-text-fill: -color-fg-muted;");

        Button snapshot1 = new Button("1F");
        snapshot1.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        snapshot1.setOnAction(e -> overlord.setSnapshotInterval(1));

        Button snapshot10 = new Button("10F");
        snapshot10.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        snapshot10.setOnAction(e -> overlord.setSnapshotInterval(10));

        Button snapshot60 = new Button("60F");
        snapshot60.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        snapshot60.setOnAction(e -> overlord.setSnapshotInterval(60));

        Button forceSnapshot = new Button("FORCE");
        forceSnapshot.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        forceSnapshot.setOnAction(e -> overlord.forceSnapshot());

        toolbar.getChildren().addAll(
                debugModeToggle, separator1,
                pauseButton, stepButton, resetButton, separator2,
                snapshotLabel, snapshot1, snapshot10, snapshot60, forceSnapshot
        );

        return toolbar;
    }

    private void setupCallbacks(InstructionPanel instructionPanel, MemoryPanel memoryPanel) {
        instructionPanel.setBreakpointCallback((address, enabled) -> {
            if (enabled) {
                if (overlord.getBreakpoints().containsKey(address)) {
                    overlord.setBreakpointEnabled(address, true);
                } else {
                    overlord.addBreakpoint(address);
                }
            } else {
                if (instructionPanel.getEnabledBreakpoints().contains(address)) {
                    overlord.setBreakpointEnabled(address, false);
                } else {
                    overlord.removeBreakpoint(address);
                }
            }
        });

        instructionPanel.syncBreakpoints(overlord.getBreakpoints());

        memoryPanel.setRangeCallback((start, end) -> {
            overlord.setSnapshotRange(start, end);
            return overlord.getCore().getCPU().setSnapshotRange(start, end);
        });
    }

    private void startSnapshotUpdater() {
        AnimationTimer snapshotTimer = new javafx.animation.AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (overlord.isDebugModeEnabled()) {
                    if (now - lastUpdate >= 100_000_000) {
                        updatePanelsFromSnapshot();
                        lastUpdate = now;
                    }
                }
            }
        };
        snapshotTimer.start();
    }

    private void updatePanelsFromSnapshot() {
        snapshot.Snapshot snap = overlord.getSnapshot();
        if (snap == null) {
            return;
        }

        snapshot.RegisterSnapshot regSnap = snap.registers();
        registerPanel.updateRegisters(
                regSnap.a(), regSnap.b(), regSnap.c(), regSnap.d(),
                regSnap.e(), regSnap.h(), regSnap.l(),
                regSnap.sp(), regSnap.pc()
        );

        snapshot.FlagSnapshot flagSnap = snap.flags();
        flagsInterruptsPanel.updateFlags(
                flagSnap.zero(),
                flagSnap.subtract(),
                flagSnap.halfCarry(),
                flagSnap.carry()
        );

        snapshot.InterruptSnapshot intSnap = snap.interrupts();
        flagsInterruptsPanel.updateInterrupts(
                intSnap.ieRegister(),
                intSnap.ifRegister()
        );

        snapshot.InstructionSnapshot instrSnap = snap.instructions();
        instructionPanel.updateInstructions(
                instrSnap.current(),
                instrSnap.lookahead(),
                regSnap.pc()
        );

        snapshot.MemorySnapshot memSnap = snap.memory();
        memoryPanel.updateMemory(
                memSnap.stackWindow(),
                memSnap.codeWindow(),
                memSnap.customWindow(),
                regSnap.sp(),
                regSnap.pc()
        );

        snapshot.PictureRegisterSnapshot ppuSnap = snap.ppu();
        ppuPanel.updateFromSnapshot(
                ppuSnap.lcdc(),
                ppuSnap.stat(),
                ppuSnap.scy(),
                ppuSnap.scx(),
                ppuSnap.ly(),
                ppuSnap.lyc(),
                ppuSnap.bgp(),
                ppuSnap.obp0(),
                ppuSnap.obp1(),
                ppuSnap.wy(),
                ppuSnap.wx(),
                ppuSnap.mode()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}