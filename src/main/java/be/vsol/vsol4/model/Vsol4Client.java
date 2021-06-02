package be.vsol.vsol4.model;

import be.vsol.tools.json;
import be.vsol.vsol6.model.enums.Language;

public class Vsol4Client extends Vsol4Record {
    @json private Contact contact = new Contact();
    @json private String extraInfo, via;

    // Constructors

    public Vsol4Client() {
        super("clients");
    }

    public Vsol4Client(String id, Contact contact, String extraInfo, String via) {
        this();
        this.id = id;
        this.contact = contact;
        this.extraInfo = extraInfo;
        this.via = via;
    }

    // Methods

    @Override public String[] getFilterFields() {
        return new String[] { contact.toString(), via, contact.address.toString() };
    }

    @Override public String toString() {
        return contact.toString();
    }

    // Getters

    public Contact getContact() { return contact; }

    public String getExtraInfo() { return extraInfo; }

    public String getVia() { return via; }

    // Static Classes

    public static class Contact {
        @json private String lastName, firstName, company, phone, email;
        @json private Language language;
        @json private Address address = new Address();

        // Constructors

        public Contact() { }

        public Contact(String lastName, String firstName, String company, Language language, String phone, String email, Address address) {
            this.lastName = lastName;
            this.firstName = firstName;
            this.company = company;
            this.language = language;
            this.phone = phone;
            this.email = email;
            this.address = address;
        }

        // Methods

        @Override public String toString() {
            return (lastName + " " + firstName).trim();
        }

        // Getters

        public String getLastName() { return lastName; }

        public String getFirstName() { return firstName; }

        public String getCompany() { return company; }

        public Language getLanguage() { return language; }

        public String getPhone() { return phone; }

        public String getEmail() { return email; }

        public Address getAddress() { return address; }
    }

    public static class Address {
        @json private String line, postalCode, city, country;

        // Constructors

        public Address() { }

        public Address(String line, String postalCode, String city, String country) {
            this.line = line;
            this.postalCode = postalCode;
            this.city = city;
            this.country = country;
        }

        // Methods

        @Override public String toString() {
            return be.vsol.util.Address.format(line, postalCode, city, country);
        }

        // Getters

        public String getLine() { return line; }

        public String getPostalCode() { return postalCode; }

        public String getCity() { return city; }

        public String getCountry() { return country; }
    }

}
