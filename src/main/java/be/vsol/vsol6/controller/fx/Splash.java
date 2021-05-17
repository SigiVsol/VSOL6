package be.vsol.vsol6.controller.fx;

import be.vsol.fx.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Splash extends FxController<VBox> {

    @FXML private Label lblLoading;

    @Override public void init() {
        lblLoading.setText("Loading. Please wait.");
    }

}
