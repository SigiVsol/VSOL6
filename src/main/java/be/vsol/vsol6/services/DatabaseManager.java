package be.vsol.vsol6.services;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.SQLite;
import be.vsol.database.structures.Database;
import be.vsol.tools.Service;
import be.vsol.util.Int;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.database.UserDb;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager implements Service {

    private boolean running = false;
    private final SystemDb systemDb;
    private final Database userDb;
    private final HashMap<Organization, Database> organizations = new HashMap<>();

    // Constructor

    public DatabaseManager(Map<String, String> params) {
        DbDriver driver;
        if (Vsol6.isCloud()) {
            driver = new MySQL(params.getOrDefault("host", "localhost"), Int.parse(params.get("port"), 3306), params.getOrDefault("user", "root"), params.getOrDefault("password", "LesMiserables1860"));
        } else {
            driver = new SQLite(Vsol6.getHome("data"));
        }

        systemDb = new SystemDb(driver);
        userDb = new UserDb(driver);
    }

    // Methods

    @Override public void start() {
//        LocalSystem system = systemDb.getSystems().get(Uid.getMachineUuid());
//        if (system == null) {
//            system = new LocalSystem();
//            systemDb.getSystems().save(system);
//        }
//        Vsol6.setSystem(system);

        running = true;
    }

    @Override public void stop() {
        running = false;
    }

    // Getters


    public SystemDb getDbSystem() { return systemDb; }

    @Override public boolean isRunning() { return running; }

}
