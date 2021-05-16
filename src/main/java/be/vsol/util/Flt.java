package be.vsol.util;

import java.text.NumberFormat;

public class Flt {

    public static float parse(String string, float defaultValue) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static String format(double number, int minFractionDigits, int maxFractionDigits) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumIntegerDigits(minFractionDigits);
        numberFormat.setMaximumFractionDigits(maxFractionDigits);
        return numberFormat.format(number);
    }

}
