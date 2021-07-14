package be.vsol.vsol6.model.meta;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;

public class Network extends DbRecord {

    @db private String organizationId, computerId;
    @db private int syncTime = 0;
    @db private boolean initialized = false;

    public Network() { }

    public Network(String organizationId, String computerId) {
        this.organizationId = organizationId;
        this.computerId = computerId;
    }

    @Override public String toString() {
        return organizationId + " - " + computerId;
    }

    // Getters
    public String getOrganizationId() {
        return organizationId;
    }

    public String getComputerId() {
        return computerId;
    }

    public int getSyncTime() {
        return syncTime;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized() { initialized = true;}
}
