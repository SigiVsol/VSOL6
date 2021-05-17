package be.vsol.vsol6.controller.fx.app;

import be.vsol.fx.FxController;
import be.vsol.util.Icon;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class Login extends FxController {

    @FXML private Label lblTitle;
    @FXML private ImageView imgLogo;

    @Override public void init() {
        imgLogo.setImage(Icon.getImage(true, "logo", 128));
    }

    @FXML private void ok() {

    }

    @FXML private void cancel() {

    }

}
