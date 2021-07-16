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

    @Override public void updateRecords(JSONArray records) {
        for (int i = 0; i < records.length(); i++) {
            String tableName = records.getJSONObject(i).getString("tableName");
            JSONObject record = records.getJSONObject(i).getJSONObject("record");
            switch (tableName) {
                case "clients" -> clients.save(Json.get(record, Client::new));
                case "patients" -> patients.save(Json.get(record, Patient::new));
                case "studies" -> studies.save(Json.get(record, Study::new));
                case "settings" -> settings.save(Json.get(record, Setting::new));
            }
        }
    }

    // Getters

    public DbTable<Client> getClients() { return clients; }

    public DbTable<Patient> getPatients() { return patients; }

    public DbTable<Study> getStudies() { return studies; }

    public DbTable<Setting> getSettings() { return settings; }

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
}
