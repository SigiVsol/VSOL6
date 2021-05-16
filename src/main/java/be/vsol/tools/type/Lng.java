package be.vsol.tools.type;

import java.text.NumberFormat;

public class Lng {

    public static String format(long number, int minDigits, boolean grouping) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setMinimumIntegerDigits(minDigits);
        numberFormat.setGroupingUsed(grouping);

        return numberFormat.format(number);
    }

    public static long parse(String string, long defaultValue) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

}
