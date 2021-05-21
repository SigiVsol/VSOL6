package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.structures.Database;
import be.vsol.database.structures.DbTable;
import be.vsol.vsol6.model.setting.gui;

public abstract class VsolDb extends Database {

    protected DbTable<gui> settings_gui;

    public VsolDb(DbDriver driver, String name) {
        super(driver, name);

        settings_gui = new DbTable<>(this, "settings_gui", gui::new);
    }

}
