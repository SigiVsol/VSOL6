package be.vsol.vsol6.model;

import be.vsol.database.structures.DbRecord;

public abstract class Record extends DbRecord {

    public Record() { }

    public Record(String id) {
        this.id = id;
    }

    // Methods

    @Override public String toString() {
        return id;
    }

}
