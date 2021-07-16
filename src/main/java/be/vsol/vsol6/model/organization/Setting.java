package be.vsol.vsol6.model.organization;

import be.vsol.database.db;
import be.vsol.tools.json;
import be.vsol.vsol6.model.Record;

public class Setting extends Record {

    @json @db private String name, value;

    // Constructor

    public Setting() { }

    public Setting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // Getters

    public String getName() { return name; }

    public String getValue() { return value; }
}
