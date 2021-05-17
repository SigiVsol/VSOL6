package be.vsol.fx.controls;

import javafx.geometry.Pos;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class DLabel extends XLabel<Double> {

    protected int minFractionDigits = 1, maxFractionDigits = 2;
    protected RoundingMode roundingMode = RoundingMode.HALF_UP;
    protected double minValue = -Double.MAX_VALUE;
    protected double maxValue = Double.MAX_VALUE;

    public DLabel() {
        this(0.0);
    }

    public DLabel(Double value) {
        super(value);
        setAlignment(Pos.CENTER_RIGHT);
        setCaptioner();
        setValue(value);
    }

    protected void setCaptioner() {
        setCaptioner(value -> {
            NumberFormat nfOut = NumberFormat.getNumberInstance();
            nfOut.setMinimumFractionDigits(minFractionDigits);
            nfOut.setMaximumFractionDigits(maxFractionDigits);
            nfOut.setRoundingMode(roundingMode);
            return nfOut.format(value);
        });
    }

    @Override public void setValue(Double value) {
        if (value != null) {
            if (value < minValue) value = minValue;
            else if (value > maxValue) value = maxValue;
        }
        
        super.setValue(value);
    }

    public void setMinFractionDigits(int minFractionDigits) {
        this.minFractionDigits = minFractionDigits;
        setCaptioner();
    }

    public void setMaxFractionDigits(int maxFractionDigits) {
        this.maxFractionDigits = maxFractionDigits;
        setCaptioner();
    }

    public void setFractionDigits(int minAndMax) {
        setFractionDigits(minAndMax, minAndMax);
    }

    public void setFractionDigits(int min, int max) {
        this.minFractionDigits = min;
        this.maxFractionDigits = max;
        setCaptioner();
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
        setCaptioner();
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
