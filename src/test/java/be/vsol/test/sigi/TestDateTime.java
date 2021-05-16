package be.vsol.test.sigi;

import be.vsol.tools.Cal;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Locale;

public class TestDateTime {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date);

        LocalTime time = LocalTime.now();

        System.out.println(time.truncatedTo(ChronoUnit.SECONDS));

        System.out.println(new Date());


    }

}
