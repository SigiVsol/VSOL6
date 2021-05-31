package be.vsol.vsol6.model;

import be.vsol.database.annotations.db;
import be.vsol.database.structures.DbRecord;
import be.vsol.tools.json;

import java.time.Instant;

public abstract class Record extends DbRecord {

    @json @db protected Instant lastOpenedTime;

    // Constructors

    public Record() { }

    public Record(String id, Instant lastOpenedTime) {
        this.id = id;
        this.lastOpenedTime = lastOpenedTime;
    }

    // Methods

    @Override public String toString() {
        return id;
    }

    // Getters

    public Instant getLastOpenedTime() { return lastOpenedTime; }

}
