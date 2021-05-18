package be.vsol.util;

public class Str {
    public static String cutoff(String string, String head, String tail) {
        if (string.startsWith(head)) {
            string = string.substring(head.length());
        }
        if (string.endsWith(tail)) {
            string = string.substring(0, string.length() - tail.length());
        }
        return string;
    }

    public static String cutoffHead(String string, String head) {
        return cutoff(string, head, "");
    }

    public static String cutoffTail(String string, String tail) {
        return cutoff(string, "", tail);
    }

    public static String addon(String string, String head, String tail) {
        if (!string.startsWith(head)) {
            string = head + string;
        }
        if (!string.endsWith(tail)) {
            string = string + tail;
        }
        return string;
    }

    public static String addonHead(String string, String head) {
        return addon(string, head, "");
    }

    public static String addonTail(String string, String tail) {
        return addon(string, "", tail);
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

}
