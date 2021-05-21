package be.vsol.database.structures;

import java.util.Vector;
import java.util.function.Supplier;

public class DbTable<E extends DbRecord> {

    private final Database db;
    private final String name;
    private final Supplier<E> supplier;

    public DbTable(Database db, String name, Supplier<E> supplier) {
        this.db = db;
        this.name = name;
        this.supplier = supplier;

        this.db.addTable(this);

        db.getDriver().matchStructure(db.getConnection(), this);
    }

    public void save(E e) {
        if (e.getId() == null) {
            db.getDriver().insertRecord(db.getConnection(), this, e);
        }

        db.getDriver().updateRecord(db.getConnection(), this, e);
    }

    public Vector<E> getAll() { return getAll(false); }

    public Vector<E> getAll(String where) { return getAll(false, where); }

    public Vector<E> getAll(boolean inclDeleted) {
        return getAll(inclDeleted, null);
    }

    public Vector<E> getAll(boolean inclDeleted, String where) {
        Vector<E> result = new Vector<>();

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

            result.add(db.getDriver().getRecord(getDb().getConnection(), this, id));

        } rs.close();

        return result;
    }

    public E get(boolean inclDeleted, String where, E defaultValue) {
        Vector<E> all = getAll(inclDeleted, where);
        if (all.isEmpty()) return defaultValue;
        else return all.firstElement();
    }

    public E get(String id) {
        return get(true, "id = '" + id + "'", null);
    }

    @Override public String toString() {
        return name;
    }

    // Getters

    public String getName() { return name; }
    public Database getDb() { return db; }
    public Supplier<E> getSupplier() { return supplier; }

}
