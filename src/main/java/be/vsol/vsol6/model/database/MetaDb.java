package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Query;

public class MetaDb extends Database {

    private final DbTable<Organization> organizations;
    private final DbTable<Query> queries;

    public MetaDb(DbDriver driver) {
        super(driver, "metadb");

        organizations = new DbTable<>(this, "organizations", Organization::new);
        queries = new DbTable<>(this, "queries", Query::new);
    }

    // Getters

    public DbTable<Organization> getOrganizations() { return organizations; }

    public DbTable<Query> getQueries() { return queries; }
}