package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.util.Json;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.meta.*;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public void updateRecords(JSONArray updates) {
        for (int i = 0; i < updates.length(); i++) {
            String tableName = updates.getJSONObject(i).getString("tableName");
            JSONObject record = updates.getJSONObject(i).getJSONObject("record");
            switch (tableName) {
                case "organizations" -> {
                    Organization organization = Json.get(record, Organization::new);
                    this.getOrganizations().save(organization);
                }
                case "users" -> {
                    User user = Json.get(record, User::new);
                    this.getUsers().save(user);
                }
                case "roles" -> {
                    Role role = Json.get(record, Role::new);
                    this.getRoles().save(role);
                }
                case "computers" -> {
                    Computer computer = Json.get(record, Computer::new);
                    this.getComputers().save(computer);
                }
                case "networks" -> {
                    Network network = Json.get(record, Network::new);
                    this.getNetworks().save(network);
                }
                case "user_settings" -> {
                    UserSetting userSetting = Json.get(record, UserSetting::new);
                    this.getUserSettings().save(userSetting);
                }
                case "computer_settings" -> {
                    ComputerSetting computerSetting = Json.get(record, ComputerSetting::new);
                    this.getComputerSettings().save(computerSetting);
                }
            }
        }
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