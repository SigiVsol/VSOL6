package be.vsol.vsol6.model;

import be.vsol.database.db;
import be.vsol.tools.json;
import be.vsol.vsol4.model.Vsol4Organization;

public class Organization extends Record {

    @json @db private String name, description;

    // Constructors

    public Organization() { }

    // TestConstructor
    public Organization(String name) {
        this.name = name;
    }

    public Organization(Vsol4Organization vsol4Organization) {
        super(vsol4Organization.getId(), vsol4Organization.getLastOpenedDate());
        name = vsol4Organization.getName();
        description = vsol4Organization.getDescription();
    }

    // Methods

    @Override public String toString() {
        return name;
    }

    // Getters

    public String getName() { return name; }

    public String getDescription() { return description; }

    // Setters

    public void setName(String name) { this.name = name;}

    public void setDescription(String description) { this.description = description;}

}
