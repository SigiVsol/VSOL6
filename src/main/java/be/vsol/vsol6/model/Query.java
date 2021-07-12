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
        fill(query);
    }

    private void fill(String query) {
        if (query.contains("INSERT")) {
            this.tableName = query.substring(12, query.indexOf("(")).trim();

            String values = query.substring(query.lastIndexOf("(") + 2);
            this.recordId = values.substring(0, values.indexOf("\""));
        } else {
            this.tableName = query.split(" ")[1];

            String id = query.substring(query.lastIndexOf("=") + 1).trim();
            this.recordId = id.substring(1, id.length() - 1);
        }
    }

    //Getters

    public String getTableName() { return tableName; }

    public String getRecordId() { return recordId; }

    public String getQuery() { return query; }
}
