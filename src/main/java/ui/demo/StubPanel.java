package ui.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class StubPanel extends StackPane {

    public StubPanel(String title) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        this.getChildren().add(titleLabel);
        this.setAlignment(Pos.CENTER);

        this.setStyle("-fx-border-color: -color-border-default; " +
                "-fx-border-width: 2; " +
                "-fx-background-color: -color-bg-default;");
        this.setPadding(new Insets(10));
    }
}