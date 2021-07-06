package be.vsol.vsol6.controller.fx.app;

import be.vsol.fx.controls.DTextField;
import be.vsol.fx.controls.ITextField;
import be.vsol.vsol6.controller.fx.FxController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class Dialog extends FxController<DialogPane> {

    @FXML private Label lblQuestion;
    @FXML private DTextField txtDouble;
    @FXML private ITextField txtInteger;
    @FXML private TextField txt;
    @FXML private PasswordField txtPassword;
    @FXML private VBox vboxFields;

    @Override public void init() {}

    public void show() {
        Platform.runLater(() -> {
            ObservableList<Node> nodes = ctrl.getGui().getApp().getRoot().getChildren();
            for(Node n : nodes)
            {
                n.setDisable(true);
            }
            //Group group = new Group(this.getRoot());
            nodes.add(this.getRoot());});
    }

    public void setupQuestion(String question){
        vboxFields.getChildren().clear();
        lblQuestion.setText(question);
    }

    public void setupPasswordQuestion(String question){
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtPassword);
        lblQuestion.setText(question);
        txtPassword.clear();
    }

    public void setupDoubleQuestion(String question, double number) {
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtDouble);
        lblQuestion.setText(question);
        txtDouble.setValue(number);
        txtDouble.selectAll();
    }

    public void setupIntegerQuestion(String question, Integer number) {
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtInteger);
        lblQuestion.setText(question);
        txtInteger.setValue(number);
        txtInteger.selectAll();
    }

    public void setupTxtQuestion(String question, String text) {
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txt);
        lblQuestion.setText(question);
        txt.setText(text);
        txt.selectAll();
    }

    @FXML private void ok() {
        System.out.println("ok");
        cancel();
    }

    @FXML private void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }

    @FXML private void cancel() {
        System.out.println("cancel");
        Platform.runLater(() -> {
            ObservableList<Node> nodes = ctrl.getGui().getApp().getRoot().getChildren();
            for (Node n : nodes) {
                n.setDisable(false);
            }
            //Group group = new Group(this.getRoot());
            nodes.remove(this.getRoot());
        });
    }
}