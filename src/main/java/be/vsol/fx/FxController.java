package be.vsol.fx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public abstract class FxController<N extends Node> {

    private N root;

    // Methods

    public void setRoot(N root) {
        this.root = root;
    }

    public void showInStage(Stage stage, boolean runLater) {
        if (runLater) {
            Platform.runLater(() -> stage.setScene(new Scene((Parent) root)));
        } else {
            stage.setScene(new Scene((Parent) root));
        }
    }

    public void init() {
        // ready (but not necessary) to override
    }

    // Getters

    public N getRoot() { return root; }

}
