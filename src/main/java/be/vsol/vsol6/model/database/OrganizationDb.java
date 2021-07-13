package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.meta.Organization;

public class OrganizationDb extends VsolDb {

    private final DbTable<Organization> organizations;

    public OrganizationDb(DbDriver driver) {
        super(driver, "organization");

        organizations = new DbTable<>(this, "organizations", Organization::new);
    }

    // Getters

    public DbTable<Organization> getOrganizations() { return organizations; }

}
