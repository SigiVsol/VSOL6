package be.vsol.fx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public abstract class FxController {

    @FXML private StackPane root;
    protected FxConfig fxConfig;

    // Methods

    public void showInStage(Stage stage) {
        Platform.runLater(() -> {
            stage.setScene(new Scene(root));
            show();
        });
    }

    public void init() {
        // ready (but not necessary) to override
    }

    public void show() {
        // ready (but not necessary) to override
    }

    // Getters

    public StackPane getRoot() { return root; }

    // Setters

    public void setFxConfig(FxConfig fxConfig) { this.fxConfig = fxConfig; }

}
