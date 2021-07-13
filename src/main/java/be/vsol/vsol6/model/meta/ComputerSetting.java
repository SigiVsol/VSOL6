package be.vsol.vsol6.model.meta;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class ComputerSetting extends DbRecord {

    @json @db private String computerId, settingName, value;

    // Constructor

    public ComputerSetting() {
    }

    // Getters

    public String getComputerId() { return computerId; }

    public String getSettingName() { return settingName; }

    public String getValue() { return value; }
}
