package be.vsol.database.model;

import be.vsol.database.db;
import be.vsol.tools.json;

import java.time.Instant;
import java.util.Objects;

/**
 * Subclasses MUST include an empty constructor for reflection!
 */
public abstract class DbRecord {

    @json @db(primaryKey = true, length = 36, auto = true) protected String id;
    @db protected boolean deleted;
    @db(auto = true) protected Instant createdTime, updatedTime;

    public DbRecord() {

    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbRecord dbRecord = (DbRecord) o;
        return Objects.equals(id, dbRecord.id);
    }

    @Override public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() { return id; }
    public boolean isDeleted() { return deleted; }
    public Instant getCreatedTime() { return createdTime; }
    public Instant getUpdatedTime() { return updatedTime; }

    public void setId(String id) { this.id = id; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public void setCreatedTime(Instant createdTime) { this.createdTime = createdTime; }
    public void setUpdatedTime(Instant updatedTime) { this.updatedTime = updatedTime; }

}
