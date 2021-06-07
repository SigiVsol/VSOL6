package be.vsol.vsol6.controller.fx;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class Splash extends FxController<VBox> {

    @FXML private void cancel() {
        if (ctrl != null) ctrl.exit();
    }

}
