package be.vsol.util;

import be.vsol.tools.Minute;

public class Hour {

    public static long ms(int hours) {
        return Minute.ms(hours * 60);
    }

}
