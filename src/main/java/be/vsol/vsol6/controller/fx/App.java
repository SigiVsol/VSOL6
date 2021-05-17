package be.vsol.vsol6.controller.fx;

import be.vsol.fx.FxController;
import be.vsol.util.Icon;
import be.vsol.vsol6.Vsol6;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class App extends FxController {

    @FXML private HBox hbxLayout;
    @FXML private ImageView imgLogo;
    @FXML private Label lblVersion;
    @FXML private StackPane content;

    @Override public void init() {
        lblVersion.setText(Vsol6.getSig().getVersion());
        imgLogo.setImage(Icon.getImage(true, "logo", 48));

        if (fxConfig.isLeftHanded()) {
            hbxLayout.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        }
    }

    public void show() {
        show(Vsol6.getGui().getLogin());
    }

    public <E extends FxController> void show(E e) {
        Platform.runLater(() -> {
            content.getChildren().clear();
            content.getChildren().add(e.getRoot());
        });
    }

    public void showLogin() {

    }

    public void showExplorer() {

    }

    public void showViewer() {

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
