package be.vsol.vsol6.controller.fx.app;

import be.vsol.vsol6.controller.fx.FxController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class OrganizationSelection extends FxController<VBox> {

    @FXML private FlowPane nameFP;

    @Override public void init() {
    }

    public void setNames(/*String[] organizations*/) {
        String[] organizations = {"Verterinary Solutions 1", "Verterinary Solutions 2"};

        for (String organization : organizations) {
            Button button = new Button(organization);
            button.setId("organization_");
            button.getStyleClass().add("name");
            button.getStyleClass().add("extraLargeText");
            nameFP.getChildren().add(button);
        }
    }
}
