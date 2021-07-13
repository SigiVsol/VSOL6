package be.vsol.vsol6.model;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.util.Uid;

public class Computer extends DbRecord {

    @db private final String code;
    @db private String alias;

    public Computer() {
        code = Uid.getHardwareCode();
        id = code;
    }

    @Override public String toString() {
        return alias;
    }

    // Getters

    public String getCode() { return code; }

    public String getAlias() { return alias; }

}
