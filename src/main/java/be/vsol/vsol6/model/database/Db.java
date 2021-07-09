package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.PostgreSQL;
import be.vsol.database.connection.SQLite;
import be.vsol.vsol6.model.config.Config;

import java.io.File;

public class Db {

    private final DbDriver driver;

    // MetaDb
    // Vector<OrgDbs> ...

    private final SystemDb systemDb;

    private final MetaDb metaDB;

    private boolean active = false;

    public Db(Config config) {
        driver = switch (config.db.type) {
            case sqlite -> new SQLite(new File(config.app.home, "data/databases"));
            case mysql -> new MySQL(config.db.host, config.db.port, config.db.user, config.db.password);
            case postgresql -> new PostgreSQL(config.db.host, config.db.port, config.db.user, config.db.password);
        };

        systemDb = new SystemDb(driver);
        metaDB = new MetaDb(driver);
    }

    public void start() {
        driver.start();

        systemDb.connect();
        metaDB.connect();

        active = true;
    }

    public void stop() {
        active = false;
    }

    // Getters

    public SystemDb getSystemDb() { return systemDb; }

    public MetaDb getMetaDb() { return  metaDB;}

    public boolean isActive() { return active; }

}
