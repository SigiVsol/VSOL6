package be.vsol.vsol6.model.config;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.util.Bool;
import be.vsol.util.Dbl;
import be.vsol.util.Int;
import be.vsol.util.Lng;

public class Setting extends DbRecord {

    @db private String systemId, userId, organizationId;
    @db private String settingName;
    @db private String value;

    // Constructors

    public Setting() { }

    public Setting(String settingName, String value) {
        this.settingName = settingName;
        this.value = value;
    }

    public Setting(String settingName, boolean value) {
        this(settingName, "" + value);
    }

    public Setting(String settingName, int value) {
        this(settingName, "" + value);
    }

    public Setting(String settingName, long value) {
        this(settingName, "" + value);
    }

    public Setting(String settingName, float value) {
        this(settingName, "" + value);
    }

    public Setting(String settingName, double value) {
        this(settingName, "" + value);
    }

    // Methods

    @Override public String toString() {
        return settingName + " -> " + value;
    }

    public Boolean getValueAsBoolean() { return value == null ? null : Bool.parse(value, false); }

    public Integer getValueAsInteger() { return value == null ? null : Int.parse(value, 0); }

    public Long getValueAsLong() { return value == null ? null : Lng.parse(value, 0L); }

    public Double getValueAsDouble() { return value == null ? null : Dbl.parse(value, 0.0); }

    // Getters

    public String getKey() { return settingName; }

    public String getValue() { return value; }

    public String getSystemId() { return systemId; }

    public String getUserId() { return userId; }

    public String getOrganizationId() { return organizationId; }

    // Setters

    public void setKey(String settingName) { this.settingName = settingName; }

    public void setValue(String value) { this.value = value; }

    public void setSystemId(String systemId) { this.systemId = systemId; }

    public void setUserId(String userId) { this.userId = userId; }

    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

}
