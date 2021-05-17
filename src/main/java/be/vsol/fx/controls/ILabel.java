package be.vsol.fx.controls;

import javafx.geometry.Pos;

import java.text.NumberFormat;

public class ILabel extends XLabel<Integer> {

    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;

    public ILabel() {
        this(0);
    }

    public ILabel(Integer value) {
        super(value);
        setAlignment(Pos.CENTER_RIGHT);

        setCaptioner(x -> NumberFormat.getNumberInstance().format(x));

        setValue(value);
    }

    @Override public void setValue(Integer value) {
        if (value != null) {
            if (value < minValue) value = minValue;
            else if (value > maxValue) value = maxValue;
        }

        super.setValue(value);
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
}
