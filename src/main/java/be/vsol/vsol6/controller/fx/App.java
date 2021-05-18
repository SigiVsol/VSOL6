package be.vsol.vsol6.controller.fx;

import be.vsol.fx.FxController;
import be.vsol.util.Icon;
import be.vsol.vsol6.Vsol6;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class App extends FxController<StackPane> {

    @FXML private ImageView imgLogo;
    @FXML private Label lblVersion;
    @FXML private BorderPane borderPane;

    @Override public void init() {
        lblVersion.setText(Vsol6.getSig().getVersion());
        imgLogo.setImage(Icon.getImage(true, "logo", 48));
    }

    public void show() {
        show(Vsol6.getGui().getLogin());
    }

    public void show(FxController<?> controller) {
        Platform.runLater(() -> borderPane.setCenter(controller.getRoot()));
    }

    @FXML private void back() {

    }

    @FXML private void home() {

    }

    @FXML private void logout() {

    }

    @FXML private void organizations() {

    }

    @FXML private void settings() {

    }

}
