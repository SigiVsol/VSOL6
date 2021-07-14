package be.vsol.vsol6.model.organization;

import be.vsol.database.db;
import be.vsol.tools.json;
import be.vsol.vsol6.model.Record;

public class Setting extends Record {

    @json @db private String settingName, value;

    // Constructor

    public Setting() { }

    public Setting(String settingName, String value) {
        this.settingName = settingName;
        this.value = value;
    }

    // Getters

    public String getSettingName() { return settingName; }

    public String getValue() { return value; }
}
