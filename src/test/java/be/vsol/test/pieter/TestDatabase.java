package be.vsol.test.pieter;

import be.vsol.database.connection.MySQL;
import be.vsol.test.sigi.db.TestDb;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.database.UserDb;

public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("Pieter is testing the database");

        MySQL driver = new MySQL("192.168.1.185", 3306, "root", "LesMiserables1862");
        driver.start();

        //TestDb db = new TestDb(driver);
        SystemDb db = new SystemDb(driver);
        db.connect();

        System.out.println("Database tested.");

    }

}