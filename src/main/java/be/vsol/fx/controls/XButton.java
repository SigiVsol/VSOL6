package be.vsol.fx.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;

public class XButton<X> extends Button implements XComponent<X> {

    private final SimpleObjectProperty<X> value = new SimpleObjectProperty<>(null);
    private String nullCaption = "---";
    private Captioner<X> captioner = X::toString;

    public XButton() {
        setValue(null);
    }

    public XButton(X value) {
        setValue(value);
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
