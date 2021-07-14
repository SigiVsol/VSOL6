package be.vsol.vsol6.model.meta;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class Role extends DbRecord {

    public enum Type { USER, ADMIN }

    @json @db private String organizationId, userId;
    @json @db private Type type = Type.USER;

    // Constructors

    public Role() {
    }

    public Role(String organizationId, String userId, Type type) {
        this.organizationId = organizationId;
        this.userId = userId;
        this.type = type;
    }

    // Getters

    public String getUserId() { return userId; }

    public String getOrganizationId() { return organizationId; }

    public Type getType() { return type; }
}
