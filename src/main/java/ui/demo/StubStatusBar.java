package ui.demo;

import javafx.geometry.Insets;
import javafx.scene.control.Label;

public class StubStatusBar extends Label {

    public StubStatusBar() {
        this.setText("Ready");
        this.setPadding(new Insets(5, 10, 5, 10));
        this.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
    }
}