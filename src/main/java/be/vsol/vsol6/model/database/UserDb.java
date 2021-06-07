package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.User;

public class UserDb extends VsolDb {

    private final DbTable<User> users;

    // Constructors

    public UserDb(DbDriver driver) {
        super(driver, "user");

        users = new DbTable<>(this, "users", User::new);
    }

    // Getters

    public DbTable<User> getUsers() { return users; }

}
