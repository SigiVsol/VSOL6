package be.vsol.vsol6.controller.fx;

import be.vsol.fx.FxController;
import be.vsol.util.Icon;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Splash extends FxController<VBox> {

    @FXML private void cancel() {
        System.exit(0);
    }

}
