package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.structures.DbTable;
import be.vsol.vsol6.model.Organization;

public class OrganizationDb extends VsolDb {

    private final DbTable<Organization> organizations;

    public OrganizationDb(DbDriver driver) {
        super(driver, "local_system");

        organizations = new DbTable<>(this, "organizations", Organization::new);
    }

    // Getters

    public DbTable<Organization> getOrganizations() { return organizations; }

}
