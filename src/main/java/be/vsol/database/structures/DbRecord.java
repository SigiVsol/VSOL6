package be.vsol.database.structures;

import be.vsol.database.annotations.Db;

import java.time.Instant;
import java.util.Objects;

public abstract class DbRecord {

    @Db(primaryKey = true, length = 36, auto = true) protected String id;
    @Db protected boolean deleted;
    @Db(auto = true) protected Instant createdTime, updatedTime;

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
