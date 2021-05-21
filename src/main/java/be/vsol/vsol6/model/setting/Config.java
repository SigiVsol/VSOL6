package be.vsol.vsol6.model.setting;

import be.vsol.database.annotations.Db;
import be.vsol.database.structures.DbRecord;
import be.vsol.util.Log;
import be.vsol.util.Reflect;

import java.lang.reflect.Field;

public abstract class Config extends DbRecord {

    private final String tablename;
    @Db private String systemId, userId, organizationId;

    public Config(String tablename) {
        this.tablename = tablename;
    }

    // Methods

    public <E extends Config> void copy(E other) { try {
        for (Field field : Reflect.getFields(this, Db.class)) {
            if (field.getAnnotation(Db.class).auto()) continue;
            field.setAccessible(true);
            field.set(this, field.get(other));
        }
    } catch (IllegalAccessException e) { Log.trace(e); } }

    // Getters

    public String getSystemId() { return systemId; }

    public String getUserId() { return userId; }

    public String getOrganizationId() { return organizationId; }

    public String getTablename() { return tablename; }

    // Setters

    public void setSystemId(String systemId) { this.systemId = systemId; }

    public void setUserId(String userId) { this.userId = userId; }

    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

}
