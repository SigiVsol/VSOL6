package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.meta.Computer;
import be.vsol.vsol6.model.meta.Network;
import be.vsol.vsol6.model.meta.Organization;
import be.vsol.vsol6.model.Query;
import be.vsol.vsol6.model.Update;

public class MetaDb extends Database {

    private final DbTable<Organization> organizations;
    private final DbTable<Computer> computers;
    private final DbTable<Network> networks;
    private final DbTable<User> users;
    private final DbTable<Roles> roles;
    private final DbTable<Query> queries;
    private final DbTable<Update> updates;


    public MetaDb(DbDriver driver) {
        super(driver, "metadb");

        organizations = new DbTable<>(this, "organizations", Organization::new);
        computers = new DbTable<>(this, "computers", Computer::new);
        networks = new DbTable<>(this, "networks", Network::new);
        users = new DbTable<>(this, "users", User::new);
        roles = new DbTable<>(this, "roles", Roles::new);
        queries = new DbTable<>(this, "queries", Query::new);
        updates = new DbTable<>(this, "updates", Update::new);
    }

    // Getters

    public DbTable<Organization> getOrganizations() { return organizations; }

    public DbTable<Computer> getComputers() { return  computers;}

    public DbTable<Network> getNetworks() { return networks;}

    public DbTable<User> getUsers() { return users; }

    public DbTable<Roles> getRoles() { return roles; }

    public DbTable<Query> getQueries() { return queries; }

    public DbTable<Update> getUpdates() { return updates; }
}