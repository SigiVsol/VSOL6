package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.structures.Database;
import be.vsol.database.structures.DbTable;
import be.vsol.vsol6.model.setting.GuiConfig;

public abstract class VsolDb extends Database {

    protected DbTable<GuiConfig> guiConfigs;

    public VsolDb(DbDriver driver, String name) {
        super(driver, name);

        guiConfigs = new DbTable<>(this, "guiConfigs", GuiConfig::new);
    }

    // Getters

    public DbTable<GuiConfig> getGuiConfigs() { return guiConfigs; }

}
