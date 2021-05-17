package be.vsol.fx.controls;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.text.NumberFormat;
import java.text.ParseException;

public class ITextField extends TextField {
    private final SimpleIntegerProperty value = new SimpleIntegerProperty(0);

    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;
    private int step = 1;

    private boolean listenerLocked = false;

    public ITextField() {
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

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            int caretPosition = getCaretPosition();
            int caretPositionFromRight = getLength() - caretPosition;
            switch (event.getCode()) {
                case BACK_SPACE -> {
                    if (getSelectedText().isEmpty()) {
                        try {
                            deleteText(getCaretPosition() - 1, getCaretPosition());
                        } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) { }
                        positionCaret(getLength() - caretPositionFromRight);
                    } else {
                        try {
                            replaceSelection("");
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    event.consume();
                }
                case DELETE -> {
                    if (getSelectedText().isEmpty()) {
                        try {
                            deleteText(getCaretPosition(), getCaretPosition() + 1);
                        } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {
                        }
                        positionCaret(getLength() - caretPositionFromRight + 1);
                    } else {
                        try {
                            replaceSelection("");
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                    event.consume();
                }
            }
        });

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (listenerLocked) return;
            listenerLocked = true;

            String cleanText = newValue.replaceAll("[^-\\d]", "");
            if (cleanText.lastIndexOf("-") > 0)
                cleanText = (cleanText.indexOf("-") == 0 ? "-" : "") + cleanText.replace("-", "");
            try {
                long newLong = NumberFormat.getNumberInstance().parse(cleanText).longValue();
                if (newLong < minValue || newLong > maxValue) {
                    setText(oldValue == null ? "" : oldValue);
                } else {
                    setValue((int) newLong);
                }
            } catch (ParseException e) {
                setValue(0);
                setText("");
            } finally {
                if (cleanText.equals("-") && minValue < 0)
                    setText("-");
            }
            listenerLocked = false;
        });

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue && getText().isBlank())
                setValue(0);
        });
    }

    private void step(int step) {
        setValue(getValue() + step);
        positionCaret(getLength());
    }

    // Getters

    public int getValue() {
        return value.get();
    }

    public SimpleIntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        if (value < minValue) setValue(minValue);
        else if (value > maxValue) setValue(maxValue);
        else {
            this.value.set(value);

            NumberFormat nf = NumberFormat.getNumberInstance();
            setText(nf.format(value));
        }
    }

    public void addValue(int value) {
        setValue(getValue() + value);
    }

    // Setters

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setStep(int step) {
        this.step = step;
    }

}
