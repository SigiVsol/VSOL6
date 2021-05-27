package be.vsol.test.sigi;

import be.vsol.tools.json;
import be.vsol.util.Json;
import be.vsol.util.Resource;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.organization.Patient;
import org.json.JSONObject;

import java.time.LocalDate;

public class TestJson {

    public static void main(String[] args) {

//        JSONObject jsonObject = new JSONObject(Resource.getString("config/defaults.json"));
//
//        Config config = Json.get(jsonObject, Config::new);
//
//        System.out.println(Json.get(config).toString(1));

        JSONObject jsonObject = new JSONObject(); {
            jsonObject.put("name", "Sigo");
            jsonObject.put("sire", "Ayo");
            jsonObject.put("birthdate", "2020-05-06");
        }

        Patient patient = Json.get(jsonObject, Patient::new);

        System.out.println(patient.getName());
        System.out.println(patient.getBirthdate());



//        Patient patient = new Patient();
//        patient.setName("Sigi");
//        patient.setBirthdate(LocalDate.now());
//
//        JSONObject jsonObject = Json.get(patient);
//
//        System.out.println(jsonObject.toString(1));



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

