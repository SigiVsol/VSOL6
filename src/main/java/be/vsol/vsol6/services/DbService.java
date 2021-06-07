//package be.vsol.vsol6.services;
//
//import be.vsol.database.connection.DbDriver;
//import be.vsol.database.connection.MySQL;
//import be.vsol.database.connection.SQLite;
//import be.vsol.vsol6.model.Organization;
//import be.vsol.vsol6.model.config.Config;
//import be.vsol.vsol6.model.database.OrgDb;
//import be.vsol.vsol6.model.database.OrganizationDb;
//import be.vsol.vsol6.model.database.SystemDb;
//import be.vsol.vsol6.model.database.UserDb;
//
//import java.io.File;
//import java.util.HashMap;
//
//public class DbService {
//
//    private final SystemDb systemDb;
//    private final UserDb userDb;
//    private final OrganizationDb organizationDb;
//    private final HashMap<Organization, OrgDb> orgDbs = new HashMap<>();
//
//    // Constructor
//
//    public DbService(File home, Config.db db) {
////        Config.db db = session.getConfig().db;
//
////        DbDriver driver = switch (db.type) {
////            case "sqlite" -> new SQLite(new File(home, "data/databases"));
////            case "mysql" -> new MySQL(db.host, db.port, db.user, db.password);
////            default -> null;
////        };
//
////        systemDb = driver == null ? null : new SystemDb(driver);
////        userDb = driver == null ? null : new UserDb(driver);
////        organizationDb = driver == null ? null : new OrganizationDb(driver);
//    }
//
//    // Methods
//
//
//    // Getters
//
//    public SystemDb getSystemDb() { return systemDb; }
//
//    public UserDb getUserDb() { return userDb; }
//
//    public OrganizationDb getOrganizationDb() { return organizationDb; }
//}
