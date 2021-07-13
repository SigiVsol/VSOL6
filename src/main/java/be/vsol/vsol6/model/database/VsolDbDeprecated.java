package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.config.Setting;

public abstract class VsolDbDeprecated extends Database {

    protected DbTable<Setting> settings;

    // Constructors

    public VsolDbDeprecated(DbDriver driver, String name) {
        super(driver, name);

        settings = new DbTable<>(this, "settings", Setting::new);
    }

    // Getters

    public DbTable<Setting> getSettings() { return settings; }

}
