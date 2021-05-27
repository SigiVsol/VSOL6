package be.vsol.vsol4;

import be.vsol.tools.json;

public abstract class Vsol4Record {

    private final String apiName;

    @json protected String id;

    // Constructors

    public Vsol4Record(String apiName) {
        this.apiName = apiName;
    }

    // Methods

    public abstract String[] getFilterFields();

    @Override public String toString() {
        return id;
    }

    // Getters

    public String getApiName() { return apiName; }

    public String getId() { return id; }

}
