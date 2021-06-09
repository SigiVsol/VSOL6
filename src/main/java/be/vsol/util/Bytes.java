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

}
