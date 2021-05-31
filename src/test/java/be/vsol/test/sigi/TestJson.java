package be.vsol.test.sigi;

import be.vsol.tools.json;
import be.vsol.util.Json;
import be.vsol.vsol6.model.enums.Sex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class TestJson {

    public static void main(String[] args) {
//        testPrimitives();
//        testArrays();
        testEnums();
    }

    private static void testPrimitives() {


        JSONObject jsonObject = Json.get(5);

        System.out.println(jsonObject.toString(1));
    }

    private static void testArrays() {
        JSONArray jsonArray = new JSONArray(); {
            jsonArray.put(1);
            jsonArray.put(2);
            jsonArray.put(3);
        }

        Vector<Integer> ints = Json.getIntegerVector(jsonArray);

        System.out.println(jsonArray);
        System.out.println(ints);
    }

    private static void testEnums() {

        JSONObject jsonObject = new JSONObject(); {
            jsonObject.put("name", "Sigi");
            jsonObject.put("sex", "M");
        }




//        Person person = new Person("Sigi", Sex.M);
//        JSONObject jsonObject = Json.get(person);
//        System.out.println(jsonObject);

        Person copy = Json.get(jsonObject, Person::new);

        System.out.println(copy);

    }

    static class Person {
        @json String name;
        @json Sex sex;

        Person() {

        }

        Person(String name, Sex sex) {
            this.name = name;
            this.sex = sex;
        }

        @Override public String toString() {
            return name + " (" + sex + ")";
        }
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

