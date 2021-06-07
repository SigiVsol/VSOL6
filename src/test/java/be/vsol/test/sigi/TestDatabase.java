package be.vsol.test.sigi;

import be.vsol.database.connection.SQLite;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;
import be.vsol.test.sigi.db.Car;
import be.vsol.test.sigi.db.TestDb;

import java.io.File;

public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("TestDatabase");

//        MySQL driver = new MySQL(mysql.host, mysql.port, mysql.user, mysql.password);
        SQLite driver = new SQLite(new File("C:/Sandbox/db"));
        driver.start();

        TestDb db = new TestDb(driver);
        db.connect();




//        DbTable<Car> cars = new DbTable<>(db, "cars", Car::new);

//        Car car = new Car("Mercedes", "A Class", 2019);
//        cars.save(car);


//        System.out.println( cars.getAll() );

//        System.out.println(db.getTables());


    }

}
