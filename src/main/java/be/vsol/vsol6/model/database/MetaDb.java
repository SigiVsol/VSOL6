package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.*;

public class MetaDb extends Database {

    private final DbTable<Organization> organizations;
    private final DbTable<User> users;
    private final DbTable<Roles> roles;
    private final DbTable<Query> queries;
    private final DbTable<Update> updates;

    public MetaDb(DbDriver driver) {
        super(driver, "metadb");

        organizations = new DbTable<>(this, "organizations", Organization::new);
        users = new DbTable<>(this, "users", User::new);
        roles = new DbTable<>(this, "roles", Roles::new);
        queries = new DbTable<>(this, "queries", Query::new);
        updates = new DbTable<>(this, "updates", Update::new);
    }

    // Getters

    public DbTable<Organization> getOrganizations() { return organizations; }

    public DbTable<User> getUsers() { return users; }

    public DbTable<Roles> getRoles() { return roles; }

    public DbTable<Query> getQueries() { return queries; }

    public DbTable<Update> getUpdates() { return updates; }
}