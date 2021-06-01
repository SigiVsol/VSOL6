package be.vsol.vsol4;

import be.vsol.tools.json;
import be.vsol.util.Date;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Vsol4Entry extends Vsol4Record {

    @json protected String description;
    @json protected LocalDateTime date;
    @json protected Vsol4Client client = new Vsol4Client();
    @json protected Vsol4Patient patient = new Vsol4Patient();

    // Constructors

    public Vsol4Entry(String apiName) {
        super(apiName);
    }

    public Vsol4Entry(String apiName, String id, String description, LocalDateTime date, Vsol4Patient patient) {
        this(apiName);
        this.id = id;
        this.description = description;
        this.date = date;
        this.patient = patient;
        this.client = patient.getClient();
    }

    @Override public String[] getFilterFields() {
        return new String[] { client.toString(), patient.toString(), Date.format(date), description };
    }

    // Getters

    public String getDescription() { return description; }

    public LocalDateTime getDate() { return date; }

    public Vsol4Client getClient() { return client; }

    public Vsol4Patient getPatient() { return patient; }

}
