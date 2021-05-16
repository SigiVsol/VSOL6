package be.vsol.tools.type;

import java.text.NumberFormat;

public class Dbl {

    public static double parse(String string, double defaultValue) {
        try {
            return Double.parseDouble(string);
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
