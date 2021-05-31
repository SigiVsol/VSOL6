package be.vsol.vsol4;

import be.vsol.tools.json;
import be.vsol.util.Date;

import java.time.LocalDateTime;

public abstract class Vsol4Entry extends Vsol4Record {

    @json protected String description;
    @json protected LocalDateTime date;
    @json protected Vsol4Patient patient = new Vsol4Patient();

    // Constructors

    public Vsol4Entry(String apiName) {
        super(apiName);
    }

    @Override public String[] getFilterFields() {
        return new String[] { patient.getClient().toString(), patient.toString(), Date.format(date), description };
    }

    // Getters

    public String getDescription() { return description; }

    public LocalDateTime getDate() { return date; }

    public Vsol4Patient getPatient() { return patient; }

}
