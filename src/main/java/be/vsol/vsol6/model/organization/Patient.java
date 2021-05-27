package be.vsol.vsol6.model.organization;

import be.vsol.database.annotations.db;
import be.vsol.tools.json;
import be.vsol.vsol4.Vsol4Patient;
import be.vsol.vsol6.model.Record;

import java.time.LocalDate;

public class Patient extends Record {

    @json @db private String name, chip, ueln, breed, species, sire, damsire, sex, color;
    @json @db private boolean neutered;
    @json @db private LocalDate birthdate;
    @json private Client client = new Client(); // TODO for db -> create and use a reference-interface

    // Constructors

    public Patient() { }

    public Patient(Vsol4Patient vsol4Patient) {
        this.name = vsol4Patient.getName();
        this.chip = vsol4Patient.getChip();
        this.ueln = vsol4Patient.getUeln();
        this.breed = vsol4Patient.getBreed();
        this.species = vsol4Patient.getSpecies();
        this.sire = vsol4Patient.getSire();
        this.damsire = vsol4Patient.getDamsire();
        this.sex = vsol4Patient.getSex();
        this.color = vsol4Patient.getColor();
        this.neutered = vsol4Patient.isNeutered();
        this.birthdate = vsol4Patient.getBirthDate();
        this.client = new Client(vsol4Patient.getClient());
    }

    // Methods

    public Vsol4Patient getVsol4Patient() {
        return new Vsol4Patient(name, chip, ueln, breed, color, sire, damsire, sex, species, birthdate, neutered, client == null ? null : client.getVsol4Client());
    }

    @Override public String toString() {
        return name;
    }

    // Getters

    public String getName() { return name; }

    public String getChip() { return chip; }

    public String getUeln() { return ueln; }

    public String getBreed() { return breed; }

    public String getSpecies() { return species; }

    public String getSire() { return sire; }

    public String getDamsire() { return damsire; }

    public String getSex() { return sex; }

    public String getColor() { return color; }

    public boolean isNeutered() { return neutered; }

    public LocalDate getBirthdate() { return birthdate; }

    public Client getClient() { return client; }

    // Setters

    public void setName(String name) { this.name = name; }

    public void setChip(String chip) { this.chip = chip; }

    public void setUeln(String ueln) { this.ueln = ueln; }

    public void setBreed(String breed) { this.breed = breed; }

    public void setSpecies(String species) { this.species = species; }

    public void setSire(String sire) { this.sire = sire; }

    public void setDamsire(String damsire) { this.damsire = damsire; }

    public void setSex(String sex) { this.sex = sex; }

    public void setColor(String color) { this.color = color; }

    public void setNeutered(boolean neutered) { this.neutered = neutered; }

    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public void setClient(Client client) { this.client = client; }

}
