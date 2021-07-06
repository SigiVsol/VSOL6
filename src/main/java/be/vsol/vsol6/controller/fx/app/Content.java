package be.vsol.vsol6.controller.fx.app;

import be.vsol.fx.util.ImageIcon;
import be.vsol.util.Icon;
import be.vsol.vsol6.controller.fx.FxController;
import be.vsol.vsol6.controller.fx.app.Explorer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class Content extends FxController<StackPane> {

    @FXML private ImageView imgLogo;
    @FXML private Label lblVersion;
    @FXML private BorderPane borderPane;

    @Override public void init() {
        lblVersion.setText(ctrl.getSig().getVersion());
        imgLogo.setImage(ImageIcon.get(true, "logo", 48));
    }

    public void show(FxController<?> controller) {
        Platform.runLater(() -> borderPane.setCenter(controller.getRoot()));
    }

    public void start() {
        show(ctrl.getGui().getExplorer());
    }

    @FXML private void back() {

    }

    @FXML public void home() {
        ctrl.getGui().getExplorer().loadUrl("www.google.be");
    }

    @FXML private void logout() {
        ctrl.getGui().getExplorer().loadUrl("www.netflix.com");
    }

    @FXML private void organizations() {
        ctrl.getGui().getExplorer().loadUrl("www.hbvl.be");
    }

    @FXML private void settings() {
        ctrl.getGui().getExplorer().loadUrl("www.youtube.com");
    }
}