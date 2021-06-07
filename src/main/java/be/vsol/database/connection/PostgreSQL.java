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

public class PostgreSQL extends ServiceBasedDbDriver {

    public PostgreSQL(String host, int port, String user, String password) {
        super("postgresql", host, port, user, password);
    }

    @Override public void start() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:" + protocol + "://" + host + ":" + port + "/", user, password);
        } catch (SQLException | ClassNotFoundException e) {
            Log.trace(e);
        }
    }

    public Connection getConnection(String dbname) {
        boolean found = false;

        RS rs = query(null, "SELECT datname FROM pg_database");
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
            return DriverManager.getConnection("jdbc:" + protocol + "://" + host + ":" + port + "/" + dbname, user, password);
        } catch (SQLException e) {
            Log.trace(e);
            return null;
        }
    }

    @Override public <R extends DbRecord> void matchStructure(DbTable<R> dbTable) {
        Object object = dbTable.getSupplier().get();

        if (exists(dbTable.getDb(), "SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' AND schemaname != 'information_schema' AND tablename = '" + dbTable.getName() + "'")) { // the table exists -> check
            HashMap<String, DbField> dbFields = new HashMap<>();
            RS rs = query(dbTable.getDb(), "SELECT * FROM information_schema.columns WHERE table_name = '" + dbTable.getName() + "' ORDER BY ordinal_position");
            while (rs.next()) {
                String field = rs.getString("column_name");
                String type = rs.getString("data_type");
                String maxLength = rs.getString("character_maximum_length");
                String nullable = rs.getString("is_nullable");
                String defaultValue = rs.getString("column_default");

                boolean primaryKey = exists(dbTable.getDb(), "SELECT * FROM information_schema.constraint_column_usage WHERE TABLE_NAME = '" + dbTable.getName() + "' AND COLUMN_NAME = '" + field + "'");

                DbField dbField = new DbField(field, type, nullable.equals("YES"), primaryKey, defaultValue);
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





















