package be.vsol.database.structures;

import be.vsol.database.connection.DbDriver;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;

public class Database {

    private final String name;
    private final DbDriver driver;
    private Connection connection;

    private final HashMap<String, DbTable<?>> tables = new HashMap<>();

    public Database(DbDriver driver, String name) {
        this.driver = driver;
        this.name = name;
        connect();
    }

    public void connect() {
        connection = driver.getConnection(name);
    }

    public RS query(String query) {
        return driver.query(connection, query);
    }

    public void update(String query) {
        driver.update(connection, query);
    }

    public void addTable(DbTable<?> dbTable) {
        tables.put(dbTable.getName(), dbTable);
    }

    @SuppressWarnings("unchecked")
    public <E extends DbRecord> DbTable<E> getTable(String name) {
        return (DbTable<E>) tables.get(name);
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

    public HashMap<String, DbTable<?>> getTables() { return tables; }
}
