package be.vsol.util;

import be.vsol.tools.Minute;

public class Time {

    public static long ms(int duration, String unit) {
        return switch (unit) {
            case "days" -> Day.ms(duration);
            case "hours" -> Hour.ms(duration);
            case "minutes" -> Minute.ms(duration);
            case "seconds" -> Second.ms(duration);
            default -> throw new IllegalStateException("Unexpected value: " + unit);
        };
    }
}
