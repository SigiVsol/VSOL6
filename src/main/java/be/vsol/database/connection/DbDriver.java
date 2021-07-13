package be.vsol.database.connection;

import be.vsol.database.db;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbRecord;
import be.vsol.database.model.DbTable;
import be.vsol.database.model.RS;
import be.vsol.util.Log;
import be.vsol.util.Uid;
import be.vsol.vsol6.model.Query;
import be.vsol.vsol6.model.config.Setting;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Vector;

public abstract class DbDriver {

    protected final String protocol;
    protected Connection connection;

    public DbDriver(String protocol) {
        this.protocol = protocol;
    }

    public abstract void start();

    public abstract Connection getConnection(String dbname);

    public boolean exists(Database db, String sql) {
        RS rs = query(db, sql);
        boolean exists = rs.next();
        rs.close();
        return exists;
    }

    public RS query(Database db, String sql) {
//        Log.out("SQL> " + sql);
        RS rs = null;
        try {
            Statement statement = (db == null ? this.connection : db.getConnection()).createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            rs = new RS(resultSet);
        } catch (SQLException e) {
            Log.trace(e);
        }
        return rs;
    }

    public void update(Database db, String sql) {
        Log.out(protocol + (db == null ? "" : " (" + db.getName()) + ")" + " > " + sql);
        try {
            Statement statement = (db == null ? this.connection : db.getConnection()).createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            Log.trace(e);
        }
    }

    public Vector<Field> getDbFields(Object object) {
        Vector<Field> result = new Vector<>();

        Class<?> currentClass = object.getClass();

        while (!currentClass.getName().equals("java.lang.Object")) {
            Vector<Field> current = new Vector<>();

            for (Field field : currentClass.getDeclaredFields()) {
                if (field.getAnnotation(db.class) != null) {
                    current.add(field);
                }
            }

            result.addAll(0, current);

            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

    public abstract <R extends DbRecord> void matchStructure(DbTable<R> dbTable);

    public <R extends DbRecord> String insertRecord(DbTable<R> dbTable, R record) {
        if (record.getId() == null) {
            record.setId(Uid.getRandom());
        }
        record.setCreatedTime(Instant.now());

        String sql = "INSERT INTO " + dbTable.getName() + "(id, createdTime) VALUES ('" + record.getId() + "', '" + getString(record.getCreatedTime()) + "')";

        update(dbTable.getDb(), sql);

        return sql;
    }

    public <R extends DbRecord> String updateRecord(DbTable<R> dbTable, R record) {
        String query = "";

        R current = getRecord(dbTable, record.getId());

        try {
            for (Field field : getDbFields(record)) {
                String name = field.getName();
                field.setAccessible(true);

                if (field.getAnnotation(db.class).auto()) {
                    continue;
                }

                boolean currentlyNull = field.get(current) == null;

                if (field.get(record) == null) { // new value is null -> only update if the old value isn't null (type doesn't matter)
                    if (!currentlyNull) {
                        query += (query.isEmpty() ? "" : ", ") + name + " = NULL";
                    }
                } else { // new value isn't null -> check the type and update if needed
                    if (field.getType().isEnum()) {
                        if (currentlyNull || !field.get(current).equals(field.get(record))) {
                            query += (query.isEmpty() ? "" : ", ") + name + " = '" + Enum.valueOf(field.getType().asSubclass(Enum.class), field.get(record).toString()) + "'";
                        }
                    } else {
                        switch (field.getType().getSimpleName()) {
                            case "boolean" -> {
                                if (currentlyNull || field.getBoolean(current) != field.getBoolean(record)) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = " + field.getBoolean(record);
                                }
                            }
                            case "int" -> {
                                if (currentlyNull || field.getInt(current) != field.getInt(record)) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = " + field.getInt(record);
                                }
                            }
                            case "long" -> {
                                if (currentlyNull || field.getLong(current) != field.getLong(record)) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = " + field.getLong(record);
                                }
                            }
                            case "short" -> {
                                if (currentlyNull || field.getShort(current) != field.getShort(record)) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = " + field.getShort(record);
                                }
                            }
                            case "double" -> {
                                if (currentlyNull || field.getDouble(current) != field.getDouble(record)) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = " + field.getDouble(record);
                                }
                            }
                            case "float" -> {
                                if (currentlyNull || field.getFloat(current) != field.getFloat(record)) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = " + field.getFloat(record);
                                }
                            }
                            case "Boolean", "Integer", "Long", "Short", "Double", "Float" -> {
                                if (currentlyNull || !field.get(current).equals(field.get(record))) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = " + field.get(record);
                                }
                            }
                            case "String" -> {
                                if (currentlyNull || !field.get(current).equals(field.get(record))) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = '" + field.get(record) + "'";
                                }
                            }
                            case "Instant" -> { // TODO check for LocalDate, LocalTime, LocalDateTime and Instant
                                if (currentlyNull || !field.get(current).equals(field.get(record))) {
                                    query += (query.isEmpty() ? "" : ", ") + name + " = '" + getString((Instant) field.get(record)) + "'";
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }

        if (!query.isEmpty()) {
            String sql = "UPDATE " + dbTable.getName() + " SET updatedTime = '" + getString(Instant.now()) + "', " + query + " WHERE id = '" + record.getId() + "'";
            update(dbTable.getDb(), sql);
            return sql;
        }
        return null;
    }

    public <R extends DbRecord> R getRecord(DbTable<R> dbTable, String id) {
        R result = dbTable.getSupplier().get();

        RS rs = query(dbTable.getDb(), "SELECT * FROM " + dbTable.getName() + " WHERE id = '" + id + "'");
        if (rs.next()) {

            try {
                for (Field field : getDbFields(result)) {
                    String name = field.getName();
                    boolean isNull = rs.getObject(name) == null;
                    field.setAccessible(true);

                    if (field.getType().isEnum()) {
                        field.set(result, Enum.valueOf(field.getType().asSubclass(Enum.class), rs.getString(name)));
                    } else {
                        switch (field.getType().getSimpleName()) {
                            case "boolean" -> field.set(result, rs.getBoolean(name));
                            case "int" -> field.set(result, rs.getInt(name));
                            case "long" -> field.set(result, rs.getLong(name));
                            case "short" -> field.set(result, rs.getShort(name));
                            case "double" -> field.set(result, rs.getDouble(name));
                            case "float" -> field.set(result, rs.getFloat(name));

                            case "Boolean" -> field.set(result, isNull ? null : rs.getBoolean(name));
                            case "Integer" -> field.set(result, isNull ? null : rs.getInt(name));
                            case "Long" -> field.set(result, isNull ? null : rs.getLong(name));
                            case "Short" -> field.set(result, isNull ? null : rs.getShort(name));
                            case "Double" -> field.set(result, isNull ? null : rs.getDouble(name));
                            case "Float" -> field.set(result, isNull ? null : rs.getFloat(name));

                            case "String" -> field.set(result, isNull ? null : rs.getString(name));
                            case "Instant" -> field.set(result, isNull ? null : getInstant(rs.getString(name)));
                            // TODO check for LocalDate, LocalTime, LocalDateTime and Instant
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                Log.trace(e);
            }
        } rs.close();

        return result;
    }

    public String getFieldStructure(DbField dbField) {
        String result = dbField.getField() + " ";

        if (dbField.getType().equals("tinyint(1)")) {
            result += "BOOLEAN ";
        } else {
            result += dbField.getType().toUpperCase() + " ";
        }

        if (!dbField.isNullable()) {
            result += "NOT NULL ";
        }

        if (dbField.isPrimaryKey()) {
            result += "PRIMARY KEY";
        } else {
            result += "DEFAULT ";

            if (dbField.getDefaultValue() == null) {
                result += "NULL";
            } else if (dbField.getType().equals("boolean") || dbField.getType().equals("tinyint(1)")) {
                result += dbField.getDefaultValue().equals("1") ? "true" : "false";
            } else if (dbField.getType().startsWith("varchar") || dbField.getType().startsWith("datetime")) {
                result += "'" + dbField.getDefaultValue() + "'";
            } else {
                result += dbField.getDefaultValue();
            }
        }

        return result;
    }

    public String getFieldStructure(Field field, Object object) {
        String result = field.getName() + " ";

        try {
            field.setAccessible(true);
            db annotation = field.getAnnotation(db.class);

            if (field.getType().isEnum()) {
                result += "VARCHAR(" + annotation.length() + ")";
            } else {
                result += switch (field.getType().getSimpleName()) {
                    case "boolean" -> "BOOLEAN NOT NULL";
                    case "int" -> "INT NOT NULL";
                    case "long" -> "BIGINT NOT NULL";
                    case "short" -> "SMALLINT NULL";
                    case "double" -> "DOUBLE NOT NULL";
                    case "float" -> "FLOAT NOT NULL";

                    case "Boolean" -> "BOOLEAN";
                    case "Integer" -> "INT";
                    case "Long" -> "BIGINT";
                    case "Short" -> "SMALLINT";
                    case "Double" -> "DOUBLE";
                    case "Float" -> "FLOAT";

                    case "String" -> "VARCHAR(" + annotation.length() + ")";
                    case "Instant" -> "DATETIME(3)";

                    default -> "";
                };
            }

            if (annotation.primaryKey()) {
                result += " NOT NULL PRIMARY KEY";
            } else {
                result += " DEFAULT ";

                if (field.get(object) == null) {
                    result += "NULL";
                } else if (field.getType().isEnum()) {
                    result += "'" + Enum.valueOf(field.getType().asSubclass(Enum.class), field.get(object).toString()) + "'";
                } else {
                    result += switch (field.getType().getSimpleName()) {
                        case "boolean", "Boolean" -> field.getBoolean(object);
                        case "int", "Integer" -> field.getInt(object);
                        case "long", "Long" -> field.getLong(object);
                        case "short", "Short" -> field.getShort(object);
                        case "double", "Double" -> field.getDouble(object);
                        case "float", "Float" -> field.getFloat(object);

                        case "String" -> "'" + field.get(object).toString() + "'";
                        case "Instant" -> "'" + getString((Instant) field.get(object)) + "'";

                        default -> "";
                    };
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String getString(Instant instant) {
        return instant.toString().substring(0, 23).replace("T", " ");
    }

    public static Instant getInstant(String string) {
        if (string == null) return null;
        else return Instant.parse(string.replace(" ", "T") + "Z");
    }

    protected static class DbField {
        private final String field, type, defaultValue;
        private final boolean nullable, primaryKey;

        protected DbField(String field, String type, boolean nullable, boolean primaryKey, String defaultValue) {
            this.field = field;
            this.type = type;
            this.defaultValue = defaultValue;
            this.nullable = nullable;
            this.primaryKey = primaryKey;
        }
        public String getField() { return field; }
        public String getType() { return type; }
        public boolean isNullable() { return nullable; }
        public boolean isPrimaryKey() { return primaryKey; }
        public String getDefaultValue() { return defaultValue; }
    }

}
