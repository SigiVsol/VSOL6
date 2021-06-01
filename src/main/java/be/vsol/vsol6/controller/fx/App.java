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
        lblVersion.setText(gui.getSig().getVersion());
        imgLogo.setImage(Icon.getImage(true, "logo", 48));
    }

    public void show(FxController<?> controller) {
        Platform.runLater(() -> borderPane.setCenter(controller.getRoot()));
    }

    @FXML private void back() {

    }

    @FXML public void home() {
        gui.getExplorer().loadUrl("http://localhost:8100");
        show(gui.getExplorer());
    }

    @FXML private void logout() {
        show(gui.getLogin());
    }

    @FXML private void organizations() {

    }

    @FXML private void settings() {
        show(gui.getSettings());
    }

}
