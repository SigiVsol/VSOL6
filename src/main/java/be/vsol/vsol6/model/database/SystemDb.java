package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.Computer;

public class SystemDb extends VsolDb {

    private final DbTable<Computer> systems;

    public SystemDb(DbDriver driver) {
        super(driver, "local_system");

        systems = new DbTable<>(this, "systems", Computer::new);
    }

    // Getters

    public DbTable<Computer> getSystems() { return systems; }

}
