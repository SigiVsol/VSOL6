package be.vsol.vsol4;

import be.vsol.tools.json;

import java.time.Instant;

public abstract class Vsol4Record {

    private final String apiName;

    @json protected String id;
    @json protected Instant createdDate, updatedDate, lastOpenedDate;

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

    public Instant getCreatedDate() { return createdDate; }

    public Instant getUpdatedDate() { return updatedDate; }

    public Instant getLastOpenedDate() { return lastOpenedDate; }

    // Setters

    public void setId(String id) { this.id = id; }

}
