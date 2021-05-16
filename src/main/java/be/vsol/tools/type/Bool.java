package be.vsol.tools.type;

public class Bool {

    public static boolean parse(String string, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(string);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

}
