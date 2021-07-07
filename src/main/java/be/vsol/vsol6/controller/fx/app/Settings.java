package be.vsol.vsol6.controller.fx.app;

import be.vsol.fx.util.ImageIcon;
import be.vsol.vsol6.controller.fx.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Settings extends FxController<VBox> {

    @FXML private Label lblTitle;
    @FXML private ImageView imgLogo;

    @Override public void init() {
        imgLogo.setImage(ImageIcon.get(true, "logo", 128));
    }

}
