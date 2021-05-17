package be.vsol.fx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public abstract class FxController<E extends Node> {

    @FXML private E root;
    protected FxConfig fxConfig;

    // Methods

    public void showInStage(Stage stage) {
        Platform.runLater(() -> {
            stage.setScene(new Scene((Parent) root));
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

    public E getRoot() { return root; }

    // Setters

    public void setFxConfig(FxConfig fxConfig) { this.fxConfig = fxConfig; }

}
