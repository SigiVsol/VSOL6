package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.structures.Database;
import be.vsol.database.structures.DbTable;
import be.vsol.vsol6.model.config.Setting;

public abstract class VsolDb extends Database {

    protected DbTable<Setting> settings;

    // Constructors

    public VsolDb(DbDriver driver, String name) {
        super(driver, name);

        settings = new DbTable<>(this, "settings", Setting::new);
    }

    // Getters

    public DbTable<Setting> getSettings() { return settings; }

}
