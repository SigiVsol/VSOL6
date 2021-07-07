package be.vsol.vsol6.controller.fx.app;

import be.vsol.fx.controls.DTextField;
import be.vsol.fx.controls.ITextField;
import be.vsol.tools.Callback;
import be.vsol.vsol6.controller.fx.FxController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class Dialog extends FxController<DialogPane> {

    @FXML private Label lblQuestion;
    @FXML private DTextField txtDouble;
    @FXML private ITextField txtInteger;
    @FXML private TextField txtString;
    @FXML private PasswordField txtPassword;
    @FXML private VBox vboxFields;
    @FXML private Button cancel;
    private Callback callback;
    private StringCallback stringCallback;
    private IntegerCallback integerCallback;
    private DoubleCallback doubleCallback;
    private enum DialogType { Confirm, Inform, Text, Integer, Double, Password }
    private DialogType activeDialogType;

    @Override public void init() {}


    public void confirm(String question, Callback callback){
        activeDialogType = DialogType.Confirm;
        this.callback = callback;
        vboxFields.getChildren().clear();
        lblQuestion.setText(question);
        show();
    }

    public void inform(String info){
        activeDialogType = DialogType.Inform;
        vboxFields.getChildren().clear();
        lblQuestion.setText(info);
        cancel.setVisible(false);
        show();
    }

    public void getPassword(String question, StringCallback stringCallback)  {
        activeDialogType = DialogType.Password;
        this.stringCallback = stringCallback;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtPassword);
        lblQuestion.setText(question);
        txtPassword.clear();
        show();
    }

    public void getDouble(String question, double number, DoubleCallback doubleCallback) {
        activeDialogType = DialogType.Double;
        this.doubleCallback = doubleCallback;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtDouble);
        lblQuestion.setText(question);
        txtDouble.setValue(number);
        txtDouble.selectAll();
        show();
    }

    public void getInteger(String question, Integer number, IntegerCallback integerCallback) {
        activeDialogType = DialogType.Integer;
        this.integerCallback = integerCallback;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtInteger);
        lblQuestion.setText(question);
        txtInteger.setValue(number);
        txtInteger.selectAll();
        show();
    }

    public void getString(String question, String text, StringCallback stringCallback) {
        //activeDialogType = DialogType.Text;
        this.stringCallback = stringCallback;
        vboxFields.getChildren().clear();
        vboxFields.getChildren().add(txtString);
        lblQuestion.setText(question);
        txtString.setText(text);
        txtString.selectAll();
        show();
    }

    @FXML private void ok() {
        switch (activeDialogType) {

            case Text:
                stringCallback.invoke(txtString.getText());
                break;
            case Password:
                stringCallback.invoke(txtPassword.getText());
                break;
            case Integer:
                integerCallback.invoke(txtInteger.getValue());
                break;
            case Double:
                doubleCallback.invoke(txtDouble.getValue());
                break;
            default:
                cancel();
        }
        cancel();
    }

    @FXML private void cancel() {
        callback = null;
        stringCallback = null;
        integerCallback = null;
        doubleCallback = null;
        cancel.setVisible(true);
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

    private void show() {
        Platform.runLater(() -> {
            ObservableList<Node> nodes = ctrl.getGui().getApp().getRoot().getChildren();
            for(Node n : nodes)
            {
                n.setDisable(true);
            }
            nodes.add(this.getRoot());
        });
    }

    private void hide() {
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