package be.vsol.vsol6.model.meta;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class ComputerSetting extends DbRecord {

    @json @db private String computerId, key, value;

    // Constructor

    public ComputerSetting() {
    }

    // Getters

    public String getComputerId() { return computerId; }

    public String getKey() { return key; }

    public String getValue() { return value; }
}
