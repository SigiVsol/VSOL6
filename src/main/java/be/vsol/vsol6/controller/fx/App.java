package be.vsol.vsol6.controller.fx;

import be.vsol.vsol6.controller.fx.app.Content;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Session;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

public class App extends FxController<StackPane> {

    @Override public void init() {

    }

    public void startLogin() {
        Session session = ctrl.getGui().getLocalSession();

        Login login = ctrl.getGui().getLogin();

        if(session.getOrganization() == null)
        {
            Platform.runLater(() -> {
                login.setupOrganizationSelection();
                this.getRoot().getChildren().add(login.getRoot());
            });
        }else if(session.getUser() == null)
        {
            Platform.runLater(() -> {
                Organization organization = session.getOrganization();
                login.setupUserSelection(organization.getName(), organization.getId());
                this.getRoot().getChildren().add(login.getRoot());
            });
        }else{
            startContent(session.getUser().getId(), session.getOrganization().getId());
        }
    }

    public void startContent(String userId, String organizationId) {
        Content content = ctrl.getGui().getContent();
        Platform.runLater(() -> {
            this.getRoot().getChildren().add(content.getRoot());
            content.start(userId, organizationId);
        });
    }
}
