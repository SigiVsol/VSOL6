package be.vsol.vsol6.controller.fx;

import be.vsol.fx.util.ImageIcon;
import be.vsol.vsol6.controller.fx.app.Dialog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class Login extends FxController<VBox> {

    @FXML private Label lblTitle;
    @FXML private Label lblDescription;
    @FXML private FlowPane fpList;
    @FXML private Button btnBack;
    @FXML private ImageView imgLogo;
    private String selectedOrganization;
    private String selectedUser;

    @Override public void init() {
        imgLogo.setImage(ImageIcon.get(true, "logo", 128));
    }



    @FXML private void back() {
        setupOrganizationSelection();
    }

    public void setSelectedOrganization(String organization) { selectedOrganization = organization;}

    public void setupOrganizationSelection() {
        btnBack.setVisible(false);
        fpList.getChildren().clear();
        lblTitle.setText("Welcome");
        lblDescription.setText("Select an organization");

        String[] organizations = {"Verterinary Solutions 1", "Verterinary Solutions 2"};

        for (String organization : organizations) {
            Button button = new Button(organization);
            button.setId(organization);
            button.setOnAction(actionEvent -> this.onClickOrganization(button.getId()));

            button.getStyleClass().add("name");
            button.getStyleClass().add("extraLargeText");
            fpList.getChildren().add(button);
        }
    }

    public void setupUserSelection() {
        btnBack.setVisible(true);
        fpList.getChildren().clear();
        lblTitle.setText(selectedOrganization);
        lblDescription.setText("Select a user");

        String[] users = {"Pieter", "Sigi", "Juriën", "Alexander", "Jan", "Gilbert", "David",};

        for (String user : users) {
            Button button = new Button(user);
            button.setId(user);
            button.setOnAction(actionEvent -> this.onClickUser(button.getId()));

            button.getStyleClass().add("name");
            button.getStyleClass().add("extraLargeText");
            fpList.getChildren().add(button);
        }
    }

    private void onClickUser(String userId)
    {
        System.out.println(userId);
        selectedUser = userId;
        Dialog dialog = ctrl.getGui().getDialog();
        dialog.setupPasswordQuestion("Welcome " + userId + ", what's your password?");
        dialog.show();
    }

    private void onClickOrganization(String organizationId)
    {
        System.out.println(organizationId);
        selectedOrganization = organizationId;
        setupUserSelection();
    }

}
