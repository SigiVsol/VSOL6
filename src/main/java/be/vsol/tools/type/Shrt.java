package be.vsol.tools.type;

public class Shrt {

    public static short parse(String string, short defaultValue) {
        try {
            return Short.parseShort(string);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

}
