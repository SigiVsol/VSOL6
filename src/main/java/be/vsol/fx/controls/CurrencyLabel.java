package be.vsol.fx.controls;

import java.text.NumberFormat;

public class CurrencyLabel extends DLabel {

    public CurrencyLabel() {
        super();
        setNullCaption("€ ---");
        setFractionDigits(2, 2);
        setCaptioner();
        setValue(null);
    }

    @Override protected void setCaptioner() {
        setCaptioner(value -> {
            NumberFormat nfOut = NumberFormat.getCurrencyInstance();
            nfOut.setMinimumFractionDigits(minFractionDigits);
            nfOut.setMaximumFractionDigits(maxFractionDigits);
            return nfOut.format(value);
        });
    }
}
