package be.vsol.vsol6.model.meta;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.tools.json;

public class Roles extends DbRecord {

    @json @db private String userId, organizationId;
    @json @db private Role role = Role.USER;

    // Constructor

    public Roles() {
    }

    public enum Role {
        USER,
        ADMIN
    }

    // Getters

    public String getUserId() { return userId; }

    public String getOrganizationId() { return organizationId; }

    public Role getRole() { return role; }
}
