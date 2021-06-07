package be.vsol.database.model;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.db;
import be.vsol.util.Log;
import be.vsol.util.Reflect;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;

public abstract class Database {

    private final String name;
    private final DbDriver driver;
    private Connection connection;

    public Database(DbDriver driver, String name) {
        this.driver = driver;
        this.name = name;
    }

    /**
     * Connects to the database and creates/alters all its tables (reflectively searching for all member DbTable objects).
     */
    public void connect() {
        connection = driver.getConnection(name);

        try {
            for (Field field : Reflect.getFields(this, "DbTable")) {
                field.setAccessible(true);

                DbTable<?> dbTable = (DbTable<?>) field.get(this);
                driver.matchStructure(dbTable);
            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }
    }

    public RS query(String query) {
        return driver.query(this, query);
    }

    public void update(String query) {
        driver.update(this, query);
    }

    // Getters

    public DbDriver getDriver() {
        return driver;
    }

    public String getName() {
        return name;
    }

    public Connection getConnection() {
        return connection;
    }

}
