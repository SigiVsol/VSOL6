package be.vsol.vsol6.model;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class Update extends DbRecord {

    @json @db private String computerId, tableName, recordId;

    // Constructors

    public Update() { }

    public Update(String computerId, String tableName, String recordId) {
        this.computerId = computerId;
        this.tableName = tableName;
        this.recordId = recordId;
    }

    //Getters

    public String getComputerId() { return computerId; }

    public String getTableName() { return tableName; }

    public String getRecordId() { return recordId; }

}
