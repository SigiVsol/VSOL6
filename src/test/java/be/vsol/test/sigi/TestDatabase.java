package be.vsol.test.sigi;

import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.SQLite;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;
import be.vsol.test.sigi.db.Car;
import be.vsol.test.sigi.db.TestDb;

import java.io.File;

public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("TestDatabase");

        MySQL driver = new MySQL("localhost", 3306, "root", "LesMiserables1862");
//        SQLite driver = new SQLite(new File("C:/Sandbox/db"));
        driver.start();

        TestDb db = new TestDb(driver);
        db.connect();




        DbTable<Car> cars = new DbTable<>(db, "cars", Car::new);

        Car car = new Car("Mercedes", "B Class", 2019, Car.Type.Electric);
        cars.save(car);


        System.out.println( cars.getAll() );

//        System.out.println(db.getTables());


    }

}
