package be.vsol.database.connection;

import be.vsol.database.model.Database;
import be.vsol.database.model.DbRecord;
import be.vsol.database.model.DbTable;
import be.vsol.database.model.RS;
import be.vsol.util.Log;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQL extends DbDriver {

    private final String host, user, password;
    private final int port;

    public MySQL(String host, int port, String user, String password) {
        super("mysql");

        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override public void start() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:" + protocol + "://" + user + ":" + password + "@" + host + ":" + port + "?autoReconnect=true");
        } catch (SQLException | ClassNotFoundException e) {
            Log.trace(e);
        }
    }

    public Connection getConnection(String dbname) {
        boolean found = false;

        RS rs = query(null, "SHOW DATABASES");
        while (rs.next()) {
            if (dbname.equals(rs.getString(0))) {
                found = true;
                break;
            }
        } rs.close();

        if (!found) {
            update(null, "CREATE DATABASE " + dbname);
        }

        try {
            return DriverManager.getConnection("jdbc:" + protocol + "://" + user + ":" + password + "@" + host + ":" + port + "/" + dbname + "?autoReconnect=true");
        } catch (SQLException e) {
            Log.trace(e);
            return null;
        }
    }

    @Override public <R extends DbRecord> void matchStructure(DbTable<R> dbTable) {
        Object object = dbTable.getSupplier().get();

        if (exists(dbTable.getDb(), "SHOW TABLES WHERE tables_in_" + dbTable.getDb().getName() + " = '" + dbTable.getName() + "'")) { // the table exists -> check
            HashMap<String, DbField> dbFields = new HashMap<>();
            RS rs = query(dbTable.getDb(), "SHOW FIELDS FROM " + dbTable.getName());
            while (rs.next()) {
                DbField dbField = new DbField(rs.getString("Field"), rs.getString("Type"), rs.getString("Null").equals("YES"), rs.getString("Key").equals("PRI"), rs.getString("Default"));
                dbFields.put(dbField.getField(), dbField);
            } rs.close();

            for (Field field : getDbFields(object)) {
                DbField dbField = dbFields.get(field.getName());
                if (dbField != null) {
                    String dbStructure = getFieldStructure(dbField);
                    String targetStructure = getFieldStructure(field, object);

                    if (!targetStructure.equals(dbStructure)) { // the field's signature is different
                        update(dbTable.getDb(), "ALTER TABLE " + dbTable.getName() + " CHANGE COLUMN " + field.getName() + " " + targetStructure);
                    }
                } else {
                    update(dbTable.getDb(), "ALTER TABLE " + dbTable.getName() + " ADD COLUMN " + getFieldStructure(field, object));
                }
            }
        } else { // the table doesn't exist -> create
            String structure = "";
            for (Field field : getDbFields(object)) {
                if (!structure.isEmpty()) structure += ", ";
                structure += getFieldStructure(field, object);
            }

            update(dbTable.getDb(), "CREATE TABLE " + dbTable.getName() + " (" + structure + ")");
        }
    }

}





















