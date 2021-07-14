package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.meta.Organization;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import be.vsol.vsol6.model.organization.Setting;
import be.vsol.vsol6.model.organization.Study;

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

    // Getters

    public DbTable<Client> getClients() { return clients; }

    public DbTable<Patient> getPatients() { return patients; }

    public DbTable<Study> getStudies() { return studies; }

    public DbTable<Setting> getSettings() { return settings; }
}
