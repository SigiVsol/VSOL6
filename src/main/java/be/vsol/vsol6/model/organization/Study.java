package be.vsol.vsol6.model.organization;

import be.vsol.database.annotations.db;
import be.vsol.tools.json;
import be.vsol.vsol4.Vsol4Study;
import be.vsol.vsol6.model.Record;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;

public class Study extends Record {

    @json @db private String description;
    @json @db private LocalDateTime dateTime;
    @json @db private int seriesCount;
    @json private Patient patient;

    // Constructors

    public Study() { }

    public Study(Vsol4Study vsol4Study) {
        super(vsol4Study.getId(), vsol4Study.getLastOpenedDate());

        this.description = vsol4Study.getDescription();
        this.dateTime = vsol4Study.getDate();
        this.seriesCount = vsol4Study.getSeriesCount();
        this.patient = new Patient(vsol4Study.getPatient());
        this.patient.setClient(new Client(vsol4Study.getClient()));
    }

    // Methods

    public Vsol4Study getVsol4Study() {
        return new Vsol4Study(id, description, dateTime, patient == null ? null : patient.getVsol4Patient(), seriesCount);
    }

    public String getClientString() {
        return patient.getClient().toString();
    }

    public String getPatientString() {
        return patient.toString();
    }

    @Override public String toString() {
        return description;
    }

    // Static Methods

    public static Comparator<Study> getComparator(String sortField, boolean sortAsc) {
        Comparator<Study> result = switch (sortField) {
            case "client" -> Comparator.comparing(Study::getClientString);
            case "patient" -> Comparator.comparing(Study::getPatientString);
            case "date" -> Comparator.comparing(Study::getDateTime);
            case "description" -> Comparator.comparing(Study::getDescription);
            case "seriesCount" -> Comparator.comparingInt(Study::getSeriesCount);
            default -> null;
        };

        if (result == null) {
            return Collections.reverseOrder(Comparator.comparing(Study::getLastOpenedTime));
        } else {
            return sortAsc ? result : Collections.reverseOrder(result);
        }
    }

    // Getters

    public String getDescription() { return description; }

    public LocalDateTime getDateTime() { return dateTime; }

    public int getSeriesCount() { return seriesCount; }

    public Patient getPatient() { return patient; }

    // Setters

    public void setDescription(String description) { this.description = description; }

    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public void setSeriesCount(int seriesCount) { this.seriesCount = seriesCount; }

    public void setPatient(Patient patient) { this.patient = patient; }

}
