package be.vsol.tools.type;

import java.text.NumberFormat;

public class Int {

    public static String format(int number, int minDigits) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setMinimumIntegerDigits(minDigits);

        return numberFormat.format(number);
    }

    public static int parse(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

}
