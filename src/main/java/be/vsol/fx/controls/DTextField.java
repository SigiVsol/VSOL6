package be.vsol.fx.controls;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

public class DTextField extends TextField {
    private final SimpleDoubleProperty value = new SimpleDoubleProperty(0.0);

    private double minValue = -Double.MAX_VALUE;
    private double maxValue = Double.MAX_VALUE;
    private int minFractionDigits = 1, maxFractionDigits = 2;
    private RoundingMode roundingMode = RoundingMode.HALF_UP;
    private double step = 1.0;

    public DTextField() {
        setValue(getValue());

        setAlignment(Pos.CENTER_RIGHT);

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> step(step);
                case DOWN -> step(-step);
            }
        });

        setOnScroll(event -> {
            if (!isDisabled() && isEditable() /* && isFocused() */) {
                if (event.getDeltaY() > 0) {
                    step(step);
                } else {
                    step(-step);
                }
            }
        });

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                parseValue();
            }
        });

        setOnAction(event -> parseValue());
    }

    private void parseValue() {
        String text = getText();
        String decimalSeparator = String.valueOf(DecimalFormatSymbols.getInstance().getDecimalSeparator());
        if (text.contains(",") && text.contains(".")) {
            text = getText().replaceAll("[^-\\d" + decimalSeparator + "]", "");
        } else {
            text = text.replace(",", decimalSeparator).replace(".", decimalSeparator);
            text = text.replaceAll("[^-\\d" + decimalSeparator + "]", "");
        }

        try {
            setValue(NumberFormat.getNumberInstance().parse(text).doubleValue());
        } catch (ParseException e) {
            setValue(0.0);
        }

        positionCaret(getLength());
    }

    private void step(double step) {
        parseValue();
        setValue(getValue() + step);
        positionCaret(getLength());
    }

    // Getters

    public double getValue() {
        return value.get();
    }

    public SimpleDoubleProperty valueProperty() {
        return value;
    }

    // Setters

    public void setValue(double value) {
        if (value < minValue) value = minValue;
        else if (value > maxValue) value = maxValue;

        this.value.set(value);

        NumberFormat nfOut = NumberFormat.getNumberInstance();
        nfOut.setMinimumFractionDigits(minFractionDigits);
        nfOut.setMaximumFractionDigits(maxFractionDigits);
        nfOut.setRoundingMode(roundingMode);
        setText(nfOut.format(value));
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
        parseValue();
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        parseValue();
    }

    public void setMinFractionDigits(int minFractionDigits) {
        this.minFractionDigits = minFractionDigits;
        parseValue();
    }

    public void setMaxFractionDigits(int maxFractionDigits) {
        this.maxFractionDigits = maxFractionDigits;
        parseValue();
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
        parseValue();
    }

    public void setStep(double step) {
        this.step = step;
    }
}
