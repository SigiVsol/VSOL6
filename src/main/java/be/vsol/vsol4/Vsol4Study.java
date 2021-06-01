package be.vsol.vsol4;

import be.vsol.tools.json;

import java.time.LocalDateTime;

public class Vsol4Study extends Vsol4Entry {

    @json private String type, orthancUuid, status, modalityType, laptopSerial, origin;
    @json private int seriesCount;

    // Constructors

    public Vsol4Study() {
        super("studies");
    }

    public Vsol4Study(String id, String description, LocalDateTime date, Vsol4Patient patient, int seriesCount) {
        super("studies", id, description, date, patient);
        this.seriesCount = seriesCount;
    }

    // Methods

    @Override public String toString() {
        return description;
    }

    // Getters

    public String getType() { return type; }

    public String getOrthancUuid() { return orthancUuid; }

    public String getStatus() { return status; }

    public String getModalityType() { return modalityType; }

    public String getLaptopSerial() { return laptopSerial; }

    public String getOrigin() { return origin; }

    public int getSeriesCount() { return seriesCount; }

}
