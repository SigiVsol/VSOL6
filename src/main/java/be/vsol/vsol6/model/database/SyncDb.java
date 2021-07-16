package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbQuery;
import be.vsol.database.model.DbTable;
import be.vsol.vsol6.model.Update;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class SyncDb extends Database {

    protected final DbTable<DbQuery> queries;
    protected final DbTable<Update> updates;

    public SyncDb(DbDriver driver, String name) {
        super(driver, name);

        queries = new DbTable<>(this, "queries", DbQuery::new);
        updates = new DbTable<>(this, "updates", Update::new);
    }

    public void deleteQueries(JSONArray queryIds) {
        for (int i = 0; i < queryIds.length(); i++) {
            String id =  queryIds.getString(i);
            DbQuery dbQuery = queries.getById(id);
            dbQuery.setDeleted(true);
            queries.save(dbQuery);
        }
    }

    public abstract void updateRecords(JSONArray records);

    // Getters

    public DbTable<DbQuery> getQueries() { return queries; }

    public DbTable<Update> getUpdates() { return updates; }

}
