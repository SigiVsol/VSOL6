package be.vsol.vsol6.controller.fx;

import be.vsol.fx.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Splash extends FxController {

    @FXML private Label lblLoading;

    @Override public void init() {
        lblLoading.setText("Loading. Please wait.");
    }

}
