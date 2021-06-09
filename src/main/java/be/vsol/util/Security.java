package be.vsol.util;

public class Security {

    public static int getCode(String string) {
        int hashCode = string.hashCode();

        hashCode = Math.abs((int) (hashCode * 6 + 13654335L << 2));

        return hashCode;
    }



}
