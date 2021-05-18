package be.vsol.database.connection;

import be.vsol.database.structures.DbRecord;
import be.vsol.database.structures.DbTable;
import be.vsol.database.structures.RS;
import be.vsol.util.FileSys;
import be.vsol.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class SQLite extends DbDriver {
    private final File dir;

    public SQLite(File dir) {
        this.dir = dir;

        FileSys.create(dir);
    }

    @Override public Connection getConnection(String dbname) {
        File file = new File(dir, dbname + ".db");

        try {
            return DriverManager.getConnection("jdbc:sqlite://" + file.getAbsolutePath());
        } catch (SQLException e) {
            Log.trace(e);
            return null;
        }
    }

    @Override public <E extends DbRecord> void matchStructure(Connection connection, DbTable<E> dbTable) {
        Object object = dbTable.getSupplier().get();

        HashMap<String, DbField> dbFields = new HashMap<>();
        RS rs = query(connection, "PRAGMA TABLE_INFO(" + dbTable.getName() + ")");
        while (rs.next()) {
            DbField dbField = new DbField(rs.getString("name"), rs.getString("type"), !rs.getBoolean("notnull"), rs.getBoolean("pk"), rs.getString("dflt_value"));
            dbFields.put(dbField.getField(), dbField);
        } rs.close();

        if (!dbFields.isEmpty()) { // the table exists -> check
            for (Field field : getDbFields(object)) {
                DbField dbField = dbFields.get(field.getName());
                if (dbField != null) {
                    String dbStructure = getFieldStructure(dbField);
                    String targetStructure = getFieldStructure(field, object);

                    if (!targetStructure.equals(dbStructure)) { // the field's signature is different
                        /* not supported in SQLite */
                        Log.err("[Not supported in SQLite] " + "ALTER TABLE " + dbTable.getName() + " CHANGE COLUMN " + field.getName() + " " + targetStructure);
                    }
                } else {
                    update(connection, "ALTER TABLE " + dbTable.getName() + " ADD COLUMN " + getFieldStructure(field, object));
                }
            }
        } else { // the table doesn't exist -> create
            String structure = "";
            for (Field field : getDbFields(object)) {
                if (!structure.isEmpty()) structure += ", ";
                structure += getFieldStructure(field, object);
            }

            update(connection, "CREATE TABLE " + dbTable.getName() + " (" + structure + ")");
        }
    }

}
