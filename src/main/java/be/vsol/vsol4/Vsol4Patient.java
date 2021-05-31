package be.vsol.vsol4;

import be.vsol.tools.json;

import java.time.LocalDate;

public class Vsol4Patient extends Vsol4Record {

    @json private String name, chip, ueln, breed, color, sire, damsire, species;
    @json private Vsol4Sex sex;
    @json private LocalDate birthDate;
    @json private boolean neutered;
    @json private Vsol4Client client = new Vsol4Client();

    // Constructors

    public Vsol4Patient() {
        super("patients");
    }

    public Vsol4Patient(String id, String name, String chip, String ueln, String breed, String color, String sire, String damsire, Vsol4Sex sex, String species, LocalDate birthDate, boolean neutered, Vsol4Client client) {
        this();
        this.id = id;
        this.name = name;
        this.chip = chip;
        this.ueln = ueln;
        this.breed = breed;
        this.color = color;
        this.sire = sire;
        this.damsire = damsire;
        this.sex = sex;
        this.species = species;
        this.birthDate = birthDate;
        this.neutered = neutered;
        this.client = client;
    }

    // Methods

    @Override public String[] getFilterFields() {
        return new String[] { name, sire, damsire, chip, ueln };
    }

    @Override public String toString() {
        return name;
    }

    // Getters

    public String getName() { return name; }

    public String getChip() { return chip; }

    public String getUeln() { return ueln; }

    public String getBreed() { return breed; }

    public String getColor() { return color; }

    public String getSire() { return sire; }

    public String getDamsire() { return damsire; }

    public Vsol4Sex getSex() { return sex; }

    public String getSpecies() { return species; }

    public boolean isNeutered() { return neutered; }

    public Vsol4Client getClient() { return client; }

    public LocalDate getBirthDate() { return birthDate; }

}
