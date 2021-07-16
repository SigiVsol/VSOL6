package be.vsol.vsol6.model.database;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.model.Database;
import be.vsol.database.model.DbQuery;
import be.vsol.database.model.DbRecord;
import be.vsol.database.model.DbTable;
import be.vsol.util.Json;
import be.vsol.vsol6.model.Update;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

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
            DbQuery dbQuery = this.getQueries().getById(id);
            dbQuery.setDeleted(true);
            this.getQueries().save(dbQuery);
        }
    }

    public abstract void updateRecords(JSONArray updates);

    // Getters

    public DbTable<DbQuery> getQueries() { return queries; }

    public DbTable<Update> getUpdates() { return updates; }

    public void addUpdate(Vector<String> computerIds, String tableName, String recordId) {
        for(String computerId: computerIds) {
            Update update = new Update(computerId, tableName, recordId);
            updates.save(update);
        }
    }

    public abstract JSONObject getObjectByRecordId(String tableName, String recordId);

    public void executeInvolvedQueries(String recordId) {
        Vector<DbQuery> queriesToExecute = queries.getAll("recordId=" + "'" + recordId + "'", " createdTime ASC");
        for(DbQuery query : queriesToExecute) {
            update(query.getQuery());
        }
    }

    public DbQuery saveQuery(JSONObject jsonQuery) {
        DbQuery query = Json.get(jsonQuery, DbQuery::new);
        //save incoming UPDATE queries; if INSERT query, execute (once) instantly
        if (query.getType() == DbQuery.Type.UPDATE) {
            queries.save(query);
        } else {
            update(query.getQuery());
        }
        return query;
    }

    public void addUpdatesToJson(String computerId, JSONObject jsonObject) {
        Vector<Update> dbUpdates = updates.getAll("computerId=" +  "'" + computerId + "'", null);
        Vector<String> updateIds = new Vector<>();
        Set<String> recordIds = new HashSet<>();
        JSONArray jsonUpdates = new JSONArray();
        for(Update update : dbUpdates) {
            String recordId = update.getRecordId();
            if(!recordIds.contains(recordId)) {
                JSONObject object = getObjectByRecordId(update.getTableName(), recordId);
                jsonUpdates.put(object);
                recordIds.add(recordId);
            }
            updateIds.add(update.getId());
        }
        jsonObject.put("records", jsonUpdates);
        jsonObject.put("updateIds", updateIds);
    }

    protected JSONObject getObjectinJson(String tableName, DbRecord record) {
        JSONObject object = new JSONObject();
        object.put("tableName", tableName);
        object.put("record", Json.get(record));
        return object;
    }

}
