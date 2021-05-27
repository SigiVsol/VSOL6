package be.vsol.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Cal {

    public static long getMillis(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }



}
