package be.vsol.tools;

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

}
