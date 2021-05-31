package be.vsol.vsol4;

import be.vsol.tools.json;

public class Vsol4Organization extends Vsol4Record {

    @json private String name, description;
    @json private boolean defaultOrganization;

    // Constructors

    public Vsol4Organization() {
        super("organizations");
    }

    // Methods

    @Override public String toString() {
        return name;
    }

    @Override public String[] getFilterFields() {
        return new String[] { name, description };
    }

    // Getters

    public String getName() { return name; }

    public String getDescription() { return description; }

    public boolean isDefaultOrganization() { return defaultOrganization; }

}
