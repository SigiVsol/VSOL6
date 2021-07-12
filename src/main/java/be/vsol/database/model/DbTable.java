package be.vsol.database.model;

import be.vsol.vsol6.model.Query;

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

    public Vector<String> save(R r) {
        Vector<String> queries = new Vector<>();

        if (r.getId() == null) {
            queries.add(db.getDriver().insertRecord(this, r));
        } else if (!db.getDriver().exists(db, "SELECT * FROM " + getName() + " WHERE id = '" + r.getId() + "'")) {
            queries.add(db.getDriver().insertRecord(this, r));
        }

        queries.add(db.getDriver().updateRecord(this, r));

        return queries;
    }

    public Vector<R> getAll() { return getAll(false); }

    public Vector<R> getAll(String where) { return getAll(false, where); }

    public Vector<R> getAll(boolean inclDeleted) {
        return getAll(inclDeleted, null);
    }

    public Vector<R> getAll(boolean inclDeleted, String where) {
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

        RS rs = db.query(query);
        while (rs.next()) {
            String id = rs.getString("id");

            result.add(db.getDriver().getRecord(this, id));

        } rs.close();

        return result;
    }

    public R get(boolean inclDeleted, String where, R defaultValue) {
        Vector<R> all = getAll(inclDeleted, where);
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
