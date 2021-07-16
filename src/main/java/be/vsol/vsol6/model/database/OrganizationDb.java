package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.util.Json;
import be.vsol.vsol6.model.Update;
import be.vsol.vsol6.model.meta.Network;
import be.vsol.vsol6.model.meta.Organization;
import be.vsol.vsol6.model.meta.Role;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import be.vsol.vsol6.model.organization.Setting;
import be.vsol.vsol6.model.organization.Study;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Vector;

public class OrganizationDb extends SyncDb {

    private final DbTable<Client> clients;
    private final DbTable<Patient> patients;
    private final DbTable<Study> studies;
    private final DbTable<Setting> settings;

    public OrganizationDb(DbDriver driver, Organization organization) {
        super(driver, "db_" + organization.getId().replaceAll("-", "_"));

        clients =  new DbTable<>(this, "clients", Client::new);
        patients =  new DbTable<>(this, "patients", Patient::new);
        studies =  new DbTable<>(this, "studies", Study::new);
        settings =  new DbTable<>(this, "settings", Setting::new);
    }

    @Override public void updateRecords(JSONArray updates) {
        for (int i = 0; i < updates.length(); i++) {
            String tableName = updates.getJSONObject(i).getString("tableName");
            JSONObject record = updates.getJSONObject(i).getJSONObject("record");
            switch (tableName) {
                case "clients" -> {
                    Client client = Json.get(record, Client::new);
                    this.getClients().save(client);
                }
                case "patients" -> {
                    Patient patient = Json.get(record, Patient::new);
                    this.getPatients().save(patient);
                }
                case "studies" -> {
                    Study study = Json.get(record, Study::new);
                    this.getStudies().save(study);
                }
                case "settings" -> {
                    Setting setting = Json.get(record, Setting::new);
                    this.getSettings().save(setting);
                }
            }
        }
    }

    // Getters

    public DbTable<Client> getClients() { return clients; }

    public DbTable<Patient> getPatients() { return patients; }

    public DbTable<Study> getStudies() { return studies; }

    public DbTable<Setting> getSettings() { return settings; }

    @Override public JSONObject getObjectByRecordId(String tableName, String recordId) {
        JSONObject object = new JSONObject();
        object.put("tableName", tableName);

        switch (tableName) {
            case "clients" -> object.put("record", Json.get(clients.getById(recordId)));
            case "patients" -> object.put("record", Json.get(patients.getById(recordId)));
            case "studies" -> object.put("record", Json.get(studies.getById(recordId)));
            case "setting" -> object.put("record", Json.get(settings.getById(recordId)));
        }
        return object;
    }

    /**
     * Function to add all records in this DB to a JSONObject
     * @param organization  the JSONObject
     */
    public void addAllToJson(JSONObject organization) {
        JSONArray orgObjects = new JSONArray();

        for(Client client: clients.getAll()) {
            orgObjects.put(getObjectinJson("clients", client));
        }

        for(Patient patient: patients.getAll()) {
            orgObjects.put(getObjectinJson("patients", patient));
        }

        for(Setting setting: settings.getAll()) {
            orgObjects.put(getObjectinJson("settings", setting));
        }

        for(Study study: studies.getAll()) {
            orgObjects.put(getObjectinJson("studies", study));
        }

        organization.put("queryIds", new JSONArray());
        organization.put("records", orgObjects);
        organization.put("updateIds", new JSONArray());
    }

    public void handleUpdates(Vector<String> computerIds, HashMap<String,String> recordTableMap) {
        //execute all queries involved and add updates
        recordTableMap.forEach((recordId, tableName) -> {
            executeInvolvedQueries(recordId);
            addUpdate(computerIds, tableName, recordId);
        });
    }


}
