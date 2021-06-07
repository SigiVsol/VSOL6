package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.db;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.LocalSystem;

public class SystemDb extends VsolDb {

    private final DbTable<LocalSystem> systems;

    public SystemDb(DbDriver driver) {
        super(driver, "local_system");

        systems = new DbTable<>(this, "systems", LocalSystem::new);
    }

    // Getters

    public DbTable<LocalSystem> getSystems() { return systems; }

}
