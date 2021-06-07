package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.PostgreSQL;
import be.vsol.database.connection.SQLite;
import be.vsol.vsol6.model.config.Config;

import java.io.File;

public class Db {

    private final DbDriver driver;

    private final SystemDb systemDb;
    private final UserDb userDb;
    private final OrganizationDb organizationDb;

    private boolean active = false;

    public Db(Config config) {
        driver = switch (config.db.type) {
            case sqlite -> new SQLite(new File(config.app.home, "data/databases"));
            case mysql -> new MySQL(config.db.host, config.db.port, config.db.user, config.db.password);
            case postgresql -> new PostgreSQL(config.db.host, config.db.port, config.db.user, config.db.password);
        };

        systemDb = new SystemDb(driver);
        userDb = new UserDb(driver);
        organizationDb = new OrganizationDb(driver);
    }

    public void start() {
        driver.start();

        systemDb.connect();
        userDb.connect();
        organizationDb.connect();

        active = true;
    }

    public void stop() {
        active = false;
    }

    // Getters

    public SystemDb getSystemDb() { return systemDb; }

    public UserDb getUserDb() { return userDb; }

    public OrganizationDb getOrganizationDb() { return organizationDb; }

    public boolean isActive() { return active; }

}
