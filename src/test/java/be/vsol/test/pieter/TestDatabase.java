package be.vsol.test.pieter;

import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.SQLite;
import be.vsol.test.sigi.db.TestDb;
import be.vsol.vsol6.model.database.OrganizationDb;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.database.UserDb;

import java.io.File;

public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("Pieter is testing the database");

        //SQLite driver = new SQLite(new File("C:/Sandbox/db"));
        MySQL driver = new MySQL("192.168.1.185", 3306, "root", "LesMiserables1862");
        try{
            driver.start();

            SystemDb systemDb = new SystemDb(driver);
            systemDb.connect();

//            OrganizationDb organizationDb = new OrganizationDb(driver);
//            organizationDb.connect();
//
//            UserDb userDb = new UserDb(driver);
//            userDb.connect();

        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Database tested.");

    }

}