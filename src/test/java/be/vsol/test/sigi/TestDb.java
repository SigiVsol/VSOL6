package be.vsol.test.sigi;

import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.SQLite;
import be.vsol.database.structures.Database;
import be.vsol.database.structures.DbTable;
import be.vsol.test.sigi.db.Car;
import be.vsol.vsol6.model.setting.mysql;

import java.io.File;

public class TestDb {

    public static void main(String[] args) {
//        MySQL driver = new MySQL(mysql.host, mysql.port, mysql.user, mysql.password);
        SQLite driver = new SQLite(new File("C:/Sandbox/db"));

        Database db = new Database(driver, "vsol6");

        DbTable<Car> cars = new DbTable<>(db, "cars", Car::new);

//        Car car = new Car("Mercedes", "A Class", 2019);
//        cars.save(car);


        System.out.println( cars.getAll() );

//        System.out.println(db.getTables());


    }

}
