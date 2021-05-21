package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;

public class SystemDb extends VsolDb {

    public SystemDb(DbDriver driver) {
        super(driver, "local_system");
    }

}
