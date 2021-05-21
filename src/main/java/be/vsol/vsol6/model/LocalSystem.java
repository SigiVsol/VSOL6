package be.vsol.vsol6.model;

import be.vsol.database.annotations.Db;
import be.vsol.database.structures.DbRecord;
import be.vsol.util.Uid;

public class LocalSystem extends DbRecord {

    @Db private String name;


    public LocalSystem() {
        id = Uid.getMachineUuid();
    }

    @Override public String toString() {
        return "System";
    }

    // Getters

    public String getName() { return name; }

}
