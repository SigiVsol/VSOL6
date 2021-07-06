package be.vsol.vsol6.controller.fx.login;

import be.vsol.util.Log;
import be.vsol.vsol6.controller.fx.FxController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class UserSelection extends FxController<VBox> {

    @FXML private Label organization;
    @FXML private FlowPane nameFP;

    @Override public void init() {
           }

    public void setNames(/*String[] users*/) {
        String[] users = {"Pieter", "Sigi", "Juriën", "Alexander", "Jan", "Gilbert", "David"};

        for (String user : users) {
            Button button = new Button(user);
            button.setId("user_" + user);
            button.setOnAction(actionEvent -> this.onClickUser(button.getId()));

            button.getStyleClass().add("name");
            button.getStyleClass().add("extraLargeText");
            nameFP.getChildren().add(button);
        }
    }

    @FXML private void back() {
        //ctrl.getGui().setOrganizationSelection();
    }

    private void onClickUser(String userId)
    {
        //ctrl.getGui().setLogin();
    }
}