package be.vsol.test.sigi;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TestDateTime {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date);

        LocalTime time = LocalTime.now();

        System.out.println(time.truncatedTo(ChronoUnit.SECONDS));

        System.out.println(new Date());


    }

}
