package be.vsol.database.connection;

import be.vsol.database.model.Database;
import be.vsol.database.model.DbRecord;
import be.vsol.database.model.DbTable;
import be.vsol.database.model.RS;
import be.vsol.util.FileSys;
import be.vsol.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class SQLite extends FileBasedDbDriver {

    public SQLite(File dir) {
        super("sqlite", dir);
    }

    @Override public <R extends DbRecord> void matchStructure(DbTable<R> dbTable) {
        Object object = dbTable.getSupplier().get();

        HashMap<String, DbField> dbFields = new HashMap<>();
        RS rs = query(dbTable.getDb(), "PRAGMA TABLE_INFO(" + dbTable.getName() + ")");
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
                        /* not supported in SQLite */ // TODO!
                        Log.err("[Not supported in SQLite] " + "ALTER TABLE " + dbTable.getName() + " CHANGE COLUMN " + field.getName() + " " + targetStructure);
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
