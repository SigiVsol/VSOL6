package be.vsol.tools;

import be.vsol.tools.type.Dbl;
import be.vsol.tools.type.Lng;

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
}
