package be.vsol.vsol6.model;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class Query extends DbRecord {

    @json @db private String tableName, recordId, query;

    // Constructor

    public Query() { }

    //Getters

    public String getTableName() { return tableName; }

    public String getRecordId() { return recordId; }

    public String getQuery() { return query; }
}
