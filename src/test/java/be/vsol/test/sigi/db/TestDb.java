package be.vsol.test.sigi.db;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;

public class TestDb extends Database {

    private final DbTable<Car> cars;

    public TestDb(DbDriver driver) {
        super(driver, "test_db");

        cars = new DbTable<>(this, "cars", Car::new);
    }

    public DbTable<Car> getCars() { return cars; }

}
