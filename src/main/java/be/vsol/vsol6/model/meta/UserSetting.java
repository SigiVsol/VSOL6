package be.vsol.vsol6.model.meta;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class UserSetting extends DbRecord {

    @json @db private String userId, settingName, value;

    // Constructor

    public UserSetting() {
    }

    // Getters

    public String getUserId() { return userId; }

    public String getSettingName() { return settingName; }

    public String getValue() { return value; }
}
