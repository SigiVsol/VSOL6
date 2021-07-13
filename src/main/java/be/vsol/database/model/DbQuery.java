package be.vsol.database.model;

import be.vsol.database.db;
import be.vsol.tools.json;

public class DbQuery extends DbRecord {

    public enum Type { INSERT, UPDATE }

    @json @db private String tableName, recordId;
    @json @db(length = 200) private String query;
    @json @db private Type type = Type.UPDATE;

    // Constructors

    public DbQuery() { }

    public DbQuery(String tableName, String recordId, String query, Type type) {
        this.tableName = tableName;
        this.recordId = recordId;
        this.query = query.replace("'", "\"");
        this.type = type;
    }

    // Getters

    public String getTableName() { return tableName; }

    public String getRecordId() { return recordId; }

    public String getQuery() { return query; }

    public Type getType() { return type; }

}
