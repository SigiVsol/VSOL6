package be.vsol.test.sigi.db;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;

public class Car extends DbRecord {

    public static enum Type { Hybrid, Electric }

    @db(length = 100) private String brand = "";
    @db private String model;
    @db private int year;
    @db private Type type = Type.Hybrid;

    public Car() {

    }

    public Car(String brand, String model, int year, Type type) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.type = type;
    }

    @Override public String toString() {
        return brand + " (" + model + "): " + type;
    }

    public String getBrand() { return brand; }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
