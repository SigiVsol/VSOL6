package be.vsol.vsol6.controller.fx.app;

import be.vsol.fx.util.ImageIcon;
import be.vsol.vsol6.controller.fx.FxController;
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
    private String userId;
    private String organizationId;

    @Override public void init() {
        lblVersion.setText(ctrl.getSig().getVersion());
        imgLogo.setImage(ImageIcon.get(true, "logo", 48));
    }

    public void start(String userId, String organizationId) {
        this.userId = userId;
        this.organizationId = organizationId;
        BrowserView browserView = ctrl.getGui().getBrowserView();
        browserView.loadUrl("localhost:8100?userId=" + userId + "&organizationId=" + organizationId);
        show(browserView);
    }

    private void show(FxController<?> controller) {
        Platform.runLater(() -> borderPane.setCenter(controller.getRoot()));
    }

    @FXML private void back() {
        show(ctrl.getGui().getBrowserView());
    }

    @FXML public void home() {
        show(ctrl.getGui().getBrowserView());
    }

    @FXML private void logout() {
        show(ctrl.getGui().getAqs());
    }

    @FXML private void organizations() {
        show(ctrl.getGui().getBrowserView());
    }

    @FXML private void settings() {
        show(ctrl.getGui().getBrowserView());
    }
}