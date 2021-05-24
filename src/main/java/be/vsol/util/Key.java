package be.vsol.util;

import java.util.HashSet;

public class Key {

    public static HashSet<String> get(String string, char symbol) {
        HashSet<String> result = new HashSet<>();

        while (true) {
            int start = string.indexOf(symbol + "{");
            int end = string.indexOf('}');

            if (start == -1 || end == -1) {
                break;
            }

            result.add(string.substring(start + 2, end));

            string = string.substring(0, start) + string.substring(end + 1);
        }

        return result;
    }

    public static String make(String key, char symbol) {
        return symbol + "{" + key + "}";
    }

    public static String strip(String key, char symbol) {
        if (key.startsWith(symbol + "{") && key.endsWith("}")) {
            return key.substring(2, key.length() - 1);
        } else {
            return key;
        }
    }

}
