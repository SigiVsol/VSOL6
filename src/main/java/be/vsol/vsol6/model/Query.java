package be.vsol.vsol6.model;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class Query extends DbRecord {

    @json @db private String tableName, recordId, query;

    // Constructors

    public Query() { }

    public Query(String query) {
        this.query = query;
    }

    //Getters

    public String getTableName() { return tableName; }

    public String getRecordId() { return recordId; }

    public String getQuery() { return query; }
}
