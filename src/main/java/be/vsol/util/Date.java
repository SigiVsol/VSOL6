package be.vsol.util;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Date {

    public static long getMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate parse(String string, LocalDate defaultValue) {
        try {
            return LocalDate.parse(string);
        } catch (DateTimeParseException e) {
            return defaultValue;
        }
    }

    public static LocalTime parse(String string, LocalTime defaultValue) {
        try {
            return LocalTime.parse(string);
        } catch (DateTimeParseException e) {
            return defaultValue;
        }
    }

    public static LocalDateTime parse(String string, LocalDateTime defaultValue) {
        try {
            return LocalDateTime.parse(string);
        } catch (DateTimeParseException e) {
            return defaultValue;
        }
    }

    public static Instant parse(String string, Instant defaultValue) {
        try {
            return Instant.parse(string);
        } catch (DateTimeParseException e) {
            return defaultValue;
        }
    }

    public static String format(LocalDate localDate) {
        return localDate.toString();
    }

    public static String format(LocalTime localTime) {
        return localTime.truncatedTo(ChronoUnit.SECONDS).toString();
    }

    public static String format(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.truncatedTo(ChronoUnit.SECONDS).toString();
    }

    public static String format(Instant instant) {
        return instant.truncatedTo(ChronoUnit.MILLIS).toString();
    }

}
