package be.vsol.database.connection;

import be.vsol.util.FileSys;
import be.vsol.util.Log;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class FileBasedDbDriver extends DbDriver {

    protected final File dir;

    public FileBasedDbDriver(String protocol, File dir) {
        super(protocol);
        this.dir = dir;
    }

    @Override public void start() {
        FileSys.create(dir);
    }

    @Override public Connection getConnection(String dbname) {
        File file = new File(dir, dbname + ".db");

        try {
            return DriverManager.getConnection("jdbc:" + protocol + "://" + file.getAbsolutePath());
        } catch (SQLException e) {
            Log.trace(e);
            return null;
        }
    }

}
