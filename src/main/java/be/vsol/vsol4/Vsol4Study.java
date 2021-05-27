package be.vsol.vsol4;

import be.vsol.tools.json;

import java.time.LocalDateTime;

public class Vsol4Study extends Vsol4Record {

    @json private String description, type, orthancUuid, status, modalityType, laptopSerial, origin;
    @json private LocalDateTime date;
    @json private int seriesCount;
    @json private Vsol4Patient patient = new Vsol4Patient();

    // Constructors

    public Vsol4Study() {
        super("studies");
    }

    // Methods


    @Override public String[] getFilterFields() {
        return new String[] {  }; // TODO
    }

    @Override public String toString() {
        return description;
    }

    // Getters

    public String getDescription() { return description; }

    public String getType() { return type; }

    public String getOrthancUuid() { return orthancUuid; }

    public String getStatus() { return status; }

    public String getModalityType() { return modalityType; }

    public String getLaptopSerial() { return laptopSerial; }

    public String getOrigin() { return origin; }

    public int getSeriesCount() { return seriesCount; }

    public LocalDateTime getDate() { return date; }

    public Vsol4Patient getPatient() { return patient; }

}
