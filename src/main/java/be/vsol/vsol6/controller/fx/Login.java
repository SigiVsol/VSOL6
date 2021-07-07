package be.vsol.vsol6.controller.fx;

import be.vsol.fx.util.ImageIcon;
import be.vsol.vsol6.controller.fx.app.Dialog;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Session;
import be.vsol.vsol6.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.Vector;

public class Login extends FxController<VBox> {

    @FXML private Label lblTitle;
    @FXML private Label lblDescription;
    @FXML private FlowPane fpList;
    @FXML private Button btnBack;
    @FXML private ImageView imgLogo;

    @Override public void init() {
        imgLogo.setImage(ImageIcon.get(true, "logo", 128));
    }

    @FXML private void back() {
        setupOrganizationSelection();
    }

    public void setupOrganizationSelection() {
        btnBack.setVisible(false);
        fpList.getChildren().clear();
        lblTitle.setText("Welcome");
        lblDescription.setText("Select an organization");

        Vector<Organization> organizations = ctrl.getDataStorage().getOrganizations("");

        for (Organization organization : organizations) {
            Button button = new Button(organization.getName());
            button.setId(organization.getId());
            button.setOnAction(actionEvent -> this.onClickOrganization(organization.getName(),organization.getId()));

            button.getStyleClass().add("name");
            button.getStyleClass().add("extraLargeText");
            fpList.getChildren().add(button);
        }
    }

    public void setupUserSelection(String organizationName, String organizationId) {
        btnBack.setVisible(true);
        fpList.getChildren().clear();
        lblTitle.setText(organizationName);
        lblDescription.setText("Select a user");

        Vector<User> users = ctrl.getDataStorage().getUsers(organizationId, "");

        for (User user : users) {
            Button button = new Button(user.getUsername());
            button.setId(user.getId());
            button.setOnAction(actionEvent -> this.onClickUser(user.getUsername(), user.getId(), organizationId));

            button.getStyleClass().add("name");
            button.getStyleClass().add("extraLargeText");
            fpList.getChildren().add(button);
        }
    }

    private void loginUser(String userName, String userId, String password, String organizationId) {
        System.out.println("User " + userName + " wants to login with password: " + password);
        //TODO: check if login is correct
        ctrl.getGui().getApp().startContent(userId,organizationId);
    }

    private void onClickUser(String userName, String userId, String organizationId)
    {
        Dialog dialog = ctrl.getGui().getDialog();
        String question = "Welcome " + userName + ", what's your password?";
        dialog.setupPasswordQuestion(question, result -> loginUser(userName, userId, result, organizationId));
        dialog.show();
    }

    private void onClickOrganization(String organizationName, String organizationId) {
        setupUserSelection(organizationName,organizationId);
    }
}
