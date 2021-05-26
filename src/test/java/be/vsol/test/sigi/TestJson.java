package be.vsol.test.sigi;

import be.vsol.tools.json;
import be.vsol.util.Json;
import be.vsol.util.Resource;
import be.vsol.vsol6.model.config.Config;
import org.json.JSONObject;

public class TestJson {

    public static void main(String[] args) {

        JSONObject jsonObject = new JSONObject(Resource.getString("config/defaults.json"));

        Config config = Json.get(Config::new, jsonObject);

        System.out.println(Json.get(config).toString(1));


    }






//    private static class Car {
//        @json
//        private String brand, make;
//        @json
//        private int year;
//
//        public Car() {}
//
//        public Car(String brand, String make, int year) {
//            this.brand = brand;
//            this.make = make;
//            this.year = year;
//        }
//
//        @Override public String toString() {
//            return brand + " " + make + " (" + year + ")";
//        }
//    }
}

