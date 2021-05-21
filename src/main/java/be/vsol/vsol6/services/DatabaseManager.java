package be.vsol.vsol6.services;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.connection.MySQL;
import be.vsol.database.connection.PostgreSQL;
import be.vsol.database.connection.SQLite;
import be.vsol.database.structures.Database;
import be.vsol.tools.Service;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.database.UserDb;
import be.vsol.vsol6.model.setting.db;

public class DatabaseManager implements Service {

    private boolean running = false;
    private final SystemDb systemDb;
    private final Database userDb;
//    private final HashMap<Organization, Database> organizations;

    // Constructor

    public DatabaseManager() {
        DbDriver driver = switch (db.type) {
            case "mysql" -> new MySQL(db.host, db.port, db.user, db.password);
            case "postgresql" -> new PostgreSQL(db.host, db.port, db.user, db.password);
            case "sqlite" -> new SQLite(Vsol6.getHome("data"));
            default -> null;
        };
        systemDb = new SystemDb(driver);
        userDb = new UserDb(driver);
//        dbUser = new UserDb(driver);


//        organizations = new HashMap<>();
    }

    // Methods

    @Override public void start() {
//        systemDb.connect();

        running = true;
    }

    @Override public void stop() {
        running = false;
    }

    // Getters


    public SystemDb getDbSystem() { return systemDb; }

    @Override public boolean isRunning() { return running; }

}
