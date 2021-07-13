package be.vsol.database.model;

import java.util.Vector;
import java.util.function.Supplier;

public class DbTable<R extends DbRecord> {

    private final Database db;
    private final String name;
    private final Supplier<R> supplier;

    public DbTable(Database db, String name, Supplier<R> supplier) {
        this.db = db;
        this.name = name;
        this.supplier = supplier;
    }

    /**
     * [Create and] save the object
     * @param r the generic object to be saved
     * @return A vector with [the INSERT query and] the UPDATE query
     */
    public Vector<DbQuery> save(R r) {
        Vector<DbQuery> result = new Vector<>();

        if (r.getId() == null || !db.getDriver().exists(db, "SELECT * FROM " + getName() + " WHERE id = '" + r.getId() + "'")) {
            DbQuery insertQuery = db.getDriver().insertRecord(this, r);
            result.add(insertQuery);
        }

        DbQuery updateQuery = db.getDriver().updateRecord(this, r);
        result.add(updateQuery);

        return result;
    }

    public Vector<R> getAll() { return getAll(false); }

    public Vector<R> getAll(String where, String orderBy) { return getAll(false, where, orderBy); }

    public Vector<R> getAll(boolean inclDeleted) {
        return getAll(inclDeleted, null, null);
    }

    public Vector<R> getAll(boolean inclDeleted, String where, String orderBy) {
        Vector<R> result = new Vector<>();

        String query = "SELECT * FROM " + name;
        if (inclDeleted) {
            if (where != null && !where.isBlank()) {
                query += " WHERE " + where;
            }
        } else {
            query += " WHERE NOT deleted";
            if (where != null && !where.isBlank()) {
                query += " AND " + where;
            }
        }
        if (orderBy != null && !orderBy.isBlank()) {
            query += " ORDER BY " + orderBy;
        }

        RS rs = db.query(query);
        while (rs.next()) {
            String id = rs.getString("id");

            result.add(db.getDriver().getRecord(this, id));

        } rs.close();

        return result;
    }

    public R get(boolean inclDeleted, String where, R defaultValue) {
        Vector<R> all = getAll(inclDeleted, where, null);
        if (all.isEmpty()) return defaultValue;
        else return all.firstElement();
    }

    public R get(String where, R defaultValue) {
        return get(false, where, defaultValue);
    }

    public R getById(String id) {
        return get(true, "id = '" + id + "'", null);
    }

    public R get(String where) {
        return get(true, where, null);
    }

    @Override public String toString() {
        return name;
    }

    // Getters

    public String getName() { return name; }
    public Database getDb() { return db; }
    public Supplier<R> getSupplier() { return supplier; }

}
