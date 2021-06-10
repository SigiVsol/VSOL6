package be.vsol.util;

public class Bytes {
    /** returns a formatted string in B, kB, MB or GB (according to what's needed for the size) */
    public static String getSizeString(long size) {
        if (size < 1024) {
            return Lng.format(size, 1, true) + " B";
        } else if (size < 1024 * 1024) {
            return Dbl.format(size / 1024.0, 1, 2) + " kB";
        } else if (size < 1024 * 1024 * 1024) {
            return Dbl.format(size / (1024.0 * 1024.0), 1, 2) + " MB";
        } else {
            return Dbl.format(size / (1024.0 * 1024.0 * 1024.0), 1, 2) + " GB";
        }
    }

    public static String getHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (int i = bytes.length - 1; i >= 0; i--) {
            String sub = Integer.toHexString(bytes[i] & 0xff).toUpperCase();
            result.append(sub.length() == 1 ? ("0" + sub) : (sub));
        }

        return result.toString();
    }

    public static int getInt(byte[] bytes) {
        long l = getLong(bytes);
        return (int) l;
    }

    public static short getShort(byte[] bytes) {
        long l = getLong(bytes);
        return (short) l;
    }

    public static long getLong(byte[] bytes) {
        long result = 0L;

        for (int i = 0; i < bytes.length; i++) {
            result += Math.pow(256, i) * (bytes[i] & 0xff);
        }

        return result;
    }

    public static byte[] getByteArray(long number, int length) {
        while (number < 0L) {
            number = Lng.pow(256, length) + number;
        }

        byte[] result = new byte[length];

        for (int i = length - 1; i >= 0; i--) {
            result[i] = (byte) (number / Lng.pow(256, i));
            number %= Lng.pow(256, i);
        }

        return result;
    }

    public static byte toByte(char hex) {
        return switch (hex) {
            case '1' -> (byte) 1;
            case '2' -> (byte) 2;
            case '3' -> (byte) 3;
            case '4' -> (byte) 4;
            case '5' -> (byte) 5;
            case '6' -> (byte) 6;
            case '7' -> (byte) 7;
            case '8' -> (byte) 8;
            case '9' -> (byte) 9;
            case 'A', 'a' -> (byte) 10;
            case 'B', 'b' -> (byte) 11;
            case 'C', 'c' -> (byte) 12;
            case 'D', 'd' -> (byte) 13;
            case 'E', 'e' -> (byte) 14;
            case 'F', 'f' -> (byte) 15;
            default -> (byte) 0;
        };
    }

    public static byte toByte(String hex) {
        if (hex.length() == 0) hex = "00";
        else if (hex.length() == 1) hex = "0" + hex;
        else if (hex.length() > 2) hex = hex.substring(hex.length() - 2);

        return (byte) (toByte(hex.charAt(0)) * 16 + toByte(hex.charAt(1)));
    }

}
