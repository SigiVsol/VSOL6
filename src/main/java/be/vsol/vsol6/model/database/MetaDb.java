package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.meta.*;

public class MetaDb extends SyncDb {

    private final DbTable<Organization> organizations;
    private final DbTable<Computer> computers;
    private final DbTable<Network> networks;
    private final DbTable<User> users;
    private final DbTable<Role> roles;
    private final DbTable<UserSetting> userSettings;
    private final DbTable<ComputerSetting> computerSettings;

    public MetaDb(DbDriver driver) {
        super(driver, "metadb");

        organizations = new DbTable<>(this, "organizations", Organization::new);
        computers = new DbTable<>(this, "computers", Computer::new);
        networks = new DbTable<>(this, "networks", Network::new);
        users = new DbTable<>(this, "users", User::new);
        roles = new DbTable<>(this, "roles", Role::new);
        userSettings = new DbTable<>(this, "user_settings", UserSetting::new);
        computerSettings = new DbTable<>(this, "computer_settings", ComputerSetting::new);
    }

    // Getters

    public DbTable<Organization> getOrganizations() { return organizations; }

    public DbTable<Computer> getComputers() { return  computers;}

    public DbTable<Network> getNetworks() { return networks;}

    public DbTable<User> getUsers() { return users; }

    public DbTable<Role> getRoles() { return roles; }

    public DbTable<UserSetting> getUserSettings() { return userSettings; }

    public DbTable<ComputerSetting> getComputerSettings() { return computerSettings; }

}