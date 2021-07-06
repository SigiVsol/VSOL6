package be.vsol.vsol6.controller.fx;

import be.vsol.vsol6.controller.fx.app.Content;
import be.vsol.vsol6.model.Session;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

public class App extends FxController<StackPane> {

    @Override public void init() {

    }

    public void start() {
        Session session = ctrl.getLocalSession();

        Login login = ctrl.getGui().getLogin();

        if(session.getOrganization() == null)
        {
            login.setupOrganizationSelection();
            Platform.runLater(() -> {
                this.getRoot().getChildren().add(login.getRoot());
            });
        }else if(session.getUser() == null)
        {
            login.setSelectedOrganization(session.getOrganization().toString());
            login.setupUserSelection();
            Platform.runLater(() -> {
                this.getRoot().getChildren().add(login.getRoot());
            });
        }else{
            Content content = ctrl.getGui().getContent();
            Platform.runLater(() -> {
                this.getRoot().getChildren().add(content.getRoot());
                content.start();
            });
        }
    }
}
