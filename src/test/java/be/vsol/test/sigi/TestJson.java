package be.vsol.test.sigi;

import be.vsol.tools.JsonField;
import be.vsol.util.Json;
import org.json.JSONObject;

public class TestJson {

    public static void main(String[] args) {
        Car car = new Car("BMW", "2", 2016);

        JSONObject jsonObject = Json.get(car);
        System.out.println(jsonObject);

        Car two = Json.get(Car::new, jsonObject);

        System.out.println(two);
    }






    private static class Car {
        @JsonField
        private String brand, make;
        @JsonField
        private int year;

        public Car() {}

        public Car(String brand, String make, int year) {
            this.brand = brand;
            this.make = make;
            this.year = year;
        }

        @Override public String toString() {
            return brand + " " + make + " (" + year + ")";
        }
    }
}

