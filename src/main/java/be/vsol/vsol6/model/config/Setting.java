package be.vsol.vsol6.model.config;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.util.Bool;
import be.vsol.util.Dbl;
import be.vsol.util.Int;
import be.vsol.util.Lng;

public class Setting extends DbRecord {

    @db
    private String systemId, userId, organizationId;
    @db
    private String key;
    @db
    private String value;

    // Constructors

    public Setting() { }

    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Setting(String key, boolean value) {
        this(key, "" + value);
    }

    public Setting(String key, int value) {
        this(key, "" + value);
    }

    public Setting(String key, long value) {
        this(key, "" + value);
    }

    public Setting(String key, float value) {
        this(key, "" + value);
    }

    public Setting(String key, double value) {
        this(key, "" + value);
    }

    // Methods

    @Override public String toString() {
        return key + " -> " + value;
    }

    public Boolean getValueAsBoolean() { return value == null ? null : Bool.parse(value, false); }

    public Integer getValueAsInteger() { return value == null ? null : Int.parse(value, 0); }

    public Long getValueAsLong() { return value == null ? null : Lng.parse(value, 0L); }

    public Double getValueAsDouble() { return value == null ? null : Dbl.parse(value, 0.0); }

    // Getters

    public String getKey() { return key; }

    public String getValue() { return value; }

    public String getSystemId() { return systemId; }

    public String getUserId() { return userId; }

    public String getOrganizationId() { return organizationId; }

    // Setters

    public void setKey(String key) { this.key = key; }

    public void setValue(String value) { this.value = value; }

    public void setSystemId(String systemId) { this.systemId = systemId; }

    public void setUserId(String userId) { this.userId = userId; }

    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

}
