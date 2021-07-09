package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.Organization;

public class MetaDb extends Database {

    private final DbTable<Organization> organizations;

    public MetaDb(DbDriver driver) {
        super(driver, "organization");

        organizations = new DbTable<>(this, "organizations", Organization::new);
    }

    // Getters

    public DbTable<Organization> getOrganizations() { return organizations; }

}