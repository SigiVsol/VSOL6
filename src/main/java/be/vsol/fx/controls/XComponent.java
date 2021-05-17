package be.vsol.fx.controls;

import javafx.beans.property.Property;

public interface XComponent<X> {

    X getValue();

    Property<X> valueProperty();

    void setValue(X value);

    void setNullCaption(String nullCaption);

    void setCaptioner(Captioner<X> captioner);

    interface Captioner<X> {
        String getCaption(X value);
    }

}
