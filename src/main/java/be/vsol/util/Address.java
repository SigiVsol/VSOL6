package be.vsol.util;

public class Address {

    public static String format(String street, String postal, String city, String country) {
        String result = (postal + " " + city).trim();
        if (!country.isBlank()) {
            result = (result + " (" + country + ")").trim();
        }

        if (!street.isBlank()) {
            if (result.isBlank()) result = street;
            else result = street + ", " + result;
        }

        return result;
    }

}
