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
import be.vsol.vsol6.model.database.OrgDb;
import be.vsol.vsol6.model.database.OrganizationDb;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.database.UserDb;
import be.vsol.vsol6.session.Session;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DatabaseService implements Service {

    private final SystemDb systemDb;
    private final UserDb userDb;
    private final OrganizationDb organizationDb;
    private final HashMap<Organization, OrgDb> orgDbs = new HashMap<>();

    // Constructor

    public DatabaseService(File home, Session session) {
        Config.db db = session.getConfig().db;

        DbDriver driver = switch (db.type) {
            case "sqlite" -> new SQLite(new File(home, "data/databases"));
            case "mysql" -> new MySQL(db.host, db.port, db.user, db.password);
            default -> null;
        };

        systemDb = driver == null ? null : new SystemDb(driver);
        userDb = driver == null ? null : new UserDb(driver);
        organizationDb = driver == null ? null : new OrganizationDb(driver);
    }

    // Methods

    @Override public void start() { }

    @Override public void stop() {  }

    // Getters

    public SystemDb getSystemDb() { return systemDb; }

    public UserDb getUserDb() { return userDb; }

    public OrganizationDb getOrganizationDb() { return organizationDb; }
}
