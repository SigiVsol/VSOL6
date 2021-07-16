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
            DbQuery dbQuery = queries.getById(id);
            dbQuery.setDeleted(true);
            queries.save(dbQuery);
        }
    }

    public abstract void updateRecords(JSONArray records);

    // Getters

    public DbTable<DbQuery> getQueries() { return queries; }

    public DbTable<Update> getUpdates() { return updates; }

    /**
     * Function to save all queries from a JSONArray
     * @param jsonQueries   the JSONArray that contains the query objects in JSON format
     * @param response      the JSONObject representing the response and to which queryIds should be appended (as acknowledgement)
     * @return a Hashmap that contains a mapping recordIds and tableNames, for which updates must be added
     */
    public HashMap<String,String> saveQueries(JSONArray jsonQueries, JSONObject response) {
        Vector<String> queryIds = new Vector<>();
        HashMap<String,String> updateRecords = new HashMap<>();
        //save incoming UPDATE queries; if INSERT query, execute (once) instantly
        for (int i = 0; i < jsonQueries.length(); i++) {
            DbQuery query = Json.get(jsonQueries.getJSONObject(i), DbQuery::new);

            if (query.getType() == DbQuery.Type.UPDATE) {
                queries.save(query);
            } else {
                update(query.getQuery());
            }
            updateRecords.put(query.getRecordId(), query.getTableName());
            queryIds.add(query.getId());
        }

        response.put("queryIds", queryIds);
        return updateRecords;
    }

    /**
     * Function to execute all saved queries of a record
     * @param recordId  the id of the record
     */
    public void executeInvolvedQueries(String recordId) {
        Vector<DbQuery> queriesToExecute = queries.getAll("recordId=" + "'" + recordId + "'", " createdTime ASC");
        for(DbQuery query : queriesToExecute) {
            update(query.getQuery());
        }
    }

    /**
     * Function to insert a update record for each computerId
     * @param computerIds   list of computerIds for which a update record must be inserted
     * @param tableName     the tableName of the record that must be updated
     * @param recordId      tje id of the record that must be updated
     */
    public void addUpdate(Vector<String> computerIds, String tableName, String recordId) {
        for(String computerId: computerIds) {
            Update update = new Update(computerId, tableName, recordId);
            updates.save(update);
        }
    }

    /**
     * Function to add all updates of a computer to a JSONObject
     * @param computerId    the id of the computer
     * @param jsonObject    the JSONObject
     */
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

    /**
     * Function to delete updates based on ids given in a JSONArray
     * @param jsonUpdates the JSONArray containing the ids
     */
    public void deleteUpdates(JSONArray jsonUpdates) {
        for(int i = 0; i < jsonUpdates.length(); i++) {
            Update update = updates.get("id=" +  "'" + jsonUpdates.getString(i) + "'");
            update.setDeleted(true);
            updates.save(update);
        }
    }

    /**
     * Function to retrieve a JSON object of a record by Id
     * @param tableName the tableName of the record
     * @param recordId  the id of the record
     * @return a JSON object of the record
     */
    public abstract JSONObject getObjectByRecordId(String tableName, String recordId);

    /**
     * Function to retrieve a JSON object of record
     * @param tableName the tableName of the record
     * @param record    the id of the record
     * @return a JSON object of the record
     */
    protected JSONObject getObjectinJson(String tableName, DbRecord record) {
        JSONObject object = new JSONObject();
        object.put("tableName", tableName);
        object.put("record", Json.get(record));
        return object;
    }
}
