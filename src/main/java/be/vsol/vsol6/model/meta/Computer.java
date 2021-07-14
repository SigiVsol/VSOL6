package be.vsol.vsol6.model.meta;

import be.vsol.database.db;
import be.vsol.database.model.DbRecord;
import be.vsol.util.Uid;

public class Computer extends DbRecord {

    @db private final String code;
    @db private String alias;

    public Computer() {
        code = Uid.getHardwareCode();
    }

    // TestConstructor
    public Computer(String code, String alias) {
        this.code = code;
        this.alias = alias;
    }

    @Override public String toString() {
        return alias;
    }

    // Getters

    public String getCode() { return code; }

    public String getAlias() { return alias; }

}
