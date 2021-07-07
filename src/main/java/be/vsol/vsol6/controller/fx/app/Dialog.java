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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class Dialog extends FxController<DialogPane> {

    @FXML private Label lblQuestion;
    @FXML private DTextField txtDouble;
    @FXML private ITextField txtInteger;
    @FXML private TextField txt;
    @FXML private PasswordField txtPassword;
    @FXML private VBox vboxFields;
    private StringCallback stringCallback;
    private IntegerCallback integerCallback;
    private DoubleCallback doubleCallback;
    private enum DialogType { NormalQuestion, TextQuestion, IntegerQuestion, DoubleQuestion, PasswordQuestion }
    private DialogType activeDialogType;

    @Override public void init() {}

    public void show() {
            Platform.runLater(() -> {
            ObservableList<Node> nodes = ctrl.getGui().getApp().getRoot().getChildren();
            for(Node n : nodes)
            {
                n.setDisable(true);
            }
            nodes.add(this.getRoot());});
    }

    public void setupQuestion(String question){
        activeDialogType = DialogType.NormalQuestion;
        vboxFields.getChildren().clear();
        lblQuestion.setText(question);
    }

    public void setupPasswordQuestion(String question, StringCallback callback)  {
        activeDialogType = DialogType.PasswordQuestion;
        stringCallback = callback;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtPassword);
        lblQuestion.setText(question);
        txtPassword.clear();
    }

    public void setupDoubleQuestion(String question, double number) {
        activeDialogType = DialogType.DoubleQuestion;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtDouble);
        lblQuestion.setText(question);
        txtDouble.setValue(number);
        txtDouble.selectAll();
    }

    public void setupIntegerQuestion(String question, Integer number) {
        activeDialogType = DialogType.IntegerQuestion;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtInteger);
        lblQuestion.setText(question);
        txtInteger.setValue(number);
        txtInteger.selectAll();
    }

    public void setupTxtQuestion(String question, String text,  StringCallback callback) {
        activeDialogType = DialogType.TextQuestion;
        stringCallback = callback;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txt);
        lblQuestion.setText(question);
        txt.setText(text);
        txt.selectAll();
    }

    @FXML private void ok() {
        switch (activeDialogType) {
            case TextQuestion:
                stringCallback.invoke(txt.getText());
                break;
            case PasswordQuestion:
                stringCallback.invoke(txtPassword.getText());
                break;
            case IntegerQuestion:
                integerCallback.invoke(txtInteger.getValue());
                break;
            case DoubleQuestion:
                doubleCallback.invoke(txtDouble.getValue());
                break;
            default:
                cancel();
        }
        cancel();
    }

    @FXML private void cancel() {
        stringCallback = null;
        integerCallback = null;
        doubleCallback = null;
        hide();
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

    @FXML private void hide() {
        Platform.runLater(() -> {
            ObservableList<Node> nodes = ctrl.getGui().getApp().getRoot().getChildren();
            for (Node n : nodes) {
                n.setDisable(false);
            }
            nodes.remove(this.getRoot());
        });
    }

    public interface StringCallback {
        void invoke(String str);
    }

    public interface IntegerCallback {
        void invoke(Integer integer);
    }

    public interface DoubleCallback {
        void invoke(Double dbl);
    }
}