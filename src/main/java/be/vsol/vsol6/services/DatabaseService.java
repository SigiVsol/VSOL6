package be.vsol.vsol6.services;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.SQLite;
import be.vsol.database.structures.Database;
import be.vsol.tools.Service;
import be.vsol.util.Int;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.database.UserDb;
import be.vsol.vsol6.session.Session;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DatabaseService implements Service {

    private final SystemDb systemDb;
    private final Database userDb;
    private final HashMap<Organization, Database> organizations = new HashMap<>();

    // Constructor

    public DatabaseService(File home, Session session) {
        Config.db db = session.getConfig().db;

        DbDriver driver = switch (db.type) {
            case "sqlite" -> new SQLite(new File(home, "data"));
            case "mysql" -> new MySQL(db.host, db.port, db.user, db.password);
            default -> null;
        };

        systemDb = driver == null ? null : new SystemDb(driver);
        userDb = driver == null ? null : new UserDb(driver);
    }

    // Methods

    @Override public void start() { }

    @Override public void stop() {  }

    // Getters


    public SystemDb getSystem() { return systemDb; }

}
