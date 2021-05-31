package be.vsol.test.sigi;

import be.vsol.util.Json;
import be.vsol.vsol6.model.organization.Patient;
import org.json.JSONObject;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

public class TestDateTime {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date);

        LocalTime time = LocalTime.now();
        System.out.println(time.truncatedTo(ChronoUnit.SECONDS));

        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime.truncatedTo(ChronoUnit.SECONDS));

        Instant instant = Instant.now();
        System.out.println(instant.truncatedTo(ChronoUnit.MILLIS));

//        System.out.println(time.truncatedTo(ChronoUnit.SECONDS));
//
//        System.out.println(new Date());
//
//        LocalDate localDate = LocalDate.parse("2021-05-03");
//
//        System.out.println(localDate.getMonth());


        Instant x = Instant.parse("2021-05-26T23:37:06.445Z");
        System.out.println(x);

//        Instant y = Instant.parse("2021-05-27T01:35:28");
//        System.out.println(y);


        Instant a = Instant.parse("2021-01-01T14:00:00.000Z");
        Instant b = Instant.parse("2021-05-01T14:00:00.001Z");
        Instant c = Instant.parse("2021-01-01T14:00:00.002Z");

        Vector<Instant> vector = new Vector<>(); {
            vector.add(b);
            vector.add(a);
            vector.add(c);
        }

        vector.sort(Comparator.comparing(Instant::getEpochSecond));

        System.out.println(vector);



    }

}
