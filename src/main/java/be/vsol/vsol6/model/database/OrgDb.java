package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;

public class OrgDb extends Database {

    public OrgDb(DbDriver driver, String name) {
        super(driver, name);
    }
}
