package be.vsol.test.sigi.db;

import be.vsol.database.annotations.Db;
import be.vsol.database.structures.DbRecord;

public class Car extends DbRecord {

    @Db(length = 100) private String brand = "";
    @Db private String model;
    @Db private int year;

    public Car() {

    }

    public Car(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    @Override public String toString() {
        return brand + " (" + model + ")";
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
