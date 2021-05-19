package be.vsol.vsol6.services;

import be.vsol.database.structures.Database;
import be.vsol.tools.Service;
import be.vsol.vsol6.model.Organization;

import java.util.HashMap;

public class DatabaseManager implements Service {

    private boolean running = false;
    private Database system;
    private Database user;
    private final HashMap<Organization, Database> organizations;

    // Constructor

    public DatabaseManager() {
        organizations = new HashMap<>();
    }

    // Methods

    @Override public void start() {



        running = true;
    }

    @Override public void stop() {
        running = false;
    }

    // Getters

    public Database getUser() { return user; }

    public Database getSystem() { return system; }

    public Database getOrganization(Organization organization) {
        return organizations.get(organization);
    }

    @Override public boolean isRunning() { return running; }

}
