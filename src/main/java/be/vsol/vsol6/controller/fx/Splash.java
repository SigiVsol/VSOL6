package be.vsol.vsol6.controller.fx;

import be.vsol.fx.FxController;
import be.vsol.util.Lang;
import be.vsol.vsol6.model.setting.vsol6;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Splash extends FxController<VBox> {

    @FXML private Label lblLoading;

    @Override public void init() {
        lblLoading.setText(Lang.get("Loading."));
    }

    public void setText(String text) {
        Platform.runLater(() -> lblLoading.setText(text));
    }

}
