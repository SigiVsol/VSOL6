package be.vsol.fx.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class XLabel<X> extends Label implements XComponent<X>, Initializable {

    private final SimpleObjectProperty<X> value = new SimpleObjectProperty<>(null);
    private String nullCaption = "---";
    private Captioner<X> captioner = X::toString;

    public XLabel() {
        this(null);
    }

    public XLabel(X value) {
        super();
        setValue(value);
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("INIT");
    }

    @Override public X getValue() {
        return value.get();
    }

    @Override public SimpleObjectProperty<X> valueProperty() {
        return value;
    }

    // Setters

    @Override public void setValue(X value) {
        this.value.set(value);

        if (value == null) {
            setText(nullCaption);
        } else {
            setText(captioner.getCaption(value));
        }
    }

    @Override public void setNullCaption(String nullCaption) {
        this.nullCaption = nullCaption;
    }

    @Override public void setCaptioner(Captioner<X> captioner) {
        this.captioner = captioner;
    }

}
