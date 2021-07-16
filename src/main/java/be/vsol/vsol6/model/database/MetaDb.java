package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.util.Json;
import be.vsol.vsol6.model.Update;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.meta.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

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
        users = new DbTable<>(this, "users", User::new);
        roles = new DbTable<>(this, "roles", Role::new);
        computers = new DbTable<>(this, "computers", Computer::new);
        networks = new DbTable<>(this, "networks", Network::new);
        userSettings = new DbTable<>(this, "user_settings", UserSetting::new);
        computerSettings = new DbTable<>(this, "computer_settings", ComputerSetting::new);
    }

    public void updateRecords(JSONArray records) {
        for (int i = 0; i < records.length(); i++) {
            String tableName = records.getJSONObject(i).getString("tableName");
            JSONObject record = records.getJSONObject(i).getJSONObject("record");
            switch (tableName) {
                case "organizations" -> organizations.save(Json.get(record, Organization::new));
                case "users" -> users.save(Json.get(record, User::new));
                case "roles" -> roles.save(Json.get(record, Role::new));
                case "computers" -> computers.save(Json.get(record, Computer::new));
                case "networks" -> networks.save(Json.get(record, Network::new));
                case "user_settings" -> userSettings.save(Json.get(record, UserSetting::new));
                case "computer_settings" -> computerSettings.save(Json.get(record, ComputerSetting::new));
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

    @Override public JSONObject getObjectByRecordId(String tableName, String recordId) {
        JSONObject object = new JSONObject();
        object.put("tableName", tableName);

        switch (tableName) {
            case "organizations" -> object.put("record", Json.get(organizations.getById(recordId)));
            case "computer" -> object.put("record", Json.get(computers.getById(recordId)));
            case "computerSettings" -> object.put("record", Json.get(computers.getById(recordId)));
            case "roles" -> object.put("record", Json.get(roles.getById(recordId)));
            case "users" -> object.put("record", Json.get(users.getById(recordId)));
            case "userSettings" -> object.put("record", Json.get(userSettings.getById(recordId)));
            case "networks" -> object.put("record", Json.get(networks.getById(recordId)));
        }
        return object;
    }

    public void handleUpdates(String computerId, HashMap<String,String> recordTableMap) {
        recordTableMap.forEach((recordId, tableName) -> {
            executeInvolvedQueries(recordId);
            addUpdate(getMetaUpdates(computerId, tableName, recordId), tableName, recordId);
        });
    }

    /**
     * Function to retrieve all computerIds that need an update if something changes
     * @param computerId    the id of the computer
     * @param tableName     the table name that changed
     * @param recordId      the id of the record that changed
     * @return a list of computerIds that needs an update
     */
    public Vector<String> getMetaUpdates(String computerId, String tableName, String recordId) {
        Vector<String> ids = new Vector<>();
        switch (tableName) {
            case "organizations" -> ids = getUpdateOnOrganisation(computerId, recordId);
            case "users" -> ids = getUpdateOnUser(computerId, recordId);
            case "userSettings" -> {
                String userId = getUserSettings().getById(recordId).getUserId();
                ids = getUpdateOnUser(computerId, userId);
            }
            case "roles" -> {
                String organizationId = getRoles().getById(recordId).getOrganizationId();
                ids = getUpdateOnUser(computerId, organizationId);
            }
        }
        return ids;
    }

    /**
     * Function to retrieve all computerIds that need an update if something of the organization changes
     * @param computerId        the id of the computer
     * @param organizationId    the id of the organization
     * @return a list of computerIds that needs an update
     */
    public Vector<String> getUpdateOnOrganisation(String computerId, String organizationId) {
        Vector<String> ids = new Vector<>();
        Vector<Network> records = networks.getAll("organizationId='" + organizationId + "'", null);
        for(Network network: records) {
            if(!computerId.equals(network.getComputerId())) {
                ids.add(network.getComputerId());
            }
        }
        return ids;
    }

    /**
     * Function to retrieve all computerIds that need an update if something of the user changes
     * @param computerId    the id of the computer where the changes are made
     * @param userId        the id of the user
     * @return a list of computerIds that needs an update
     */
    private Vector<String> getUpdateOnUser(String computerId, String userId) {
        Vector<String> ids = new Vector<>();
        Vector<Role> records = roles.getAll("userId='" + userId + "'", null);
        for(Role role: records) {
            ids.addAll(getUpdateOnOrganisation(computerId, role.getOrganizationId()));
        }
        return ids;
    }

    /**
     * Function to add all records in this DB of the computer/organization to a JSONObject
     * @param computerId        the id of the computer
     * @param organizationId    the id of the organization
     * @param updates           the JSONObject
     */
    public void addAllToJson(String computerId, String organizationId, JSONArray updates) {
        for(Organization organization: organizations.getAll("id=" +  "'" + organizationId + "'",null)) {
            updates.put(getObjectinJson("organizations", organization));
        }

        for(Role role: roles.getAll("organizationId=" +  "'" + organizationId + "'",null)) {
            updates.put(getObjectinJson("roles", role));

            User user = users.getById(role.getUserId());
            updates.put(getObjectinJson("users", user));

            for(UserSetting userSetting: userSettings.getAll("userId=" +  "'" + role.getUserId() + "'",null)) {
                updates.put(getObjectinJson("userSettings", userSetting));
            }
        }

        //TODO: Do we need to send computer DBRecord?
        Computer computer = computers.getById(computerId);
        updates.put(getObjectinJson("computers", computer));

        for(ComputerSetting computerSetting: computerSettings.getAll("computerId=" +  "'" + computerId + "'",null)) {
            updates.put(getObjectinJson("computerSettings ", computerSetting));
        }

        //TODO: Networks doesn't have to be shared with the computers?
    }
}