package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;

public class UserDb extends VsolDb {

    public UserDb(DbDriver driver) {
        super(driver, "user");
    }

}
