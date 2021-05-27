package be.vsol.vsol6.model.organization;

import be.vsol.database.annotations.db;
import be.vsol.tools.json;
import be.vsol.vsol4.Vsol4Client;
import be.vsol.vsol6.model.Record;

public class Client extends Record {

    @json @db private String lastName, firstName, company, language, phone, email, address, postal, city, country, via, extraInfo;

    // Constructors

    public Client() { }

    public Client(Vsol4Client vsol4Client) {
        Vsol4Client.Contact contact = vsol4Client.getContact();
        Vsol4Client.Address address = contact.getAddress();

        this.lastName = contact.getLastName();
        this.firstName = contact.getFirstName();
        this.company = contact.getCompany();
        this.language = contact.getLanguage();
        this.phone = contact.getPhone();
        this.email = contact.getEmail();
        this.address = address.getLine();
        this.postal = address.getPostalCode();
        this.city = address.getCity();
        this.country = address.getCountry();
        this.via = vsol4Client.getVia();
        this.extraInfo = vsol4Client.getExtraInfo();
    }

    // Methods

    public Vsol4Client getVsol4Client() {
        return new Vsol4Client(new Vsol4Client.Contact(lastName, firstName, company, language, phone, email, new Vsol4Client.Address(address, postal, city, country)), extraInfo, via);
    }

    @Override public String toString() {
        return (lastName + " " + firstName).trim();
    }

    // Getters

    public String getLastName() { return lastName; }

    public String getFirstName() { return firstName; }

    public String getCompany() { return company; }

    public String getLanguage() { return language; }

    public String getPhone() { return phone; }

    public String getEmail() { return email; }

    public String getAddress() { return address; }

    public String getPostal() { return postal; }

    public String getCity() { return city; }

    public String getCountry() { return country; }

    public String getVia() { return via; }

    public String getExtraInfo() { return extraInfo; }

    // Setters

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setCompany(String company) { this.company = company; }

    public void setLanguage(String language) { this.language = language; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setEmail(String email) { this.email = email; }

    public void setAddress(String address) { this.address = address; }

    public void setPostal(String postal) { this.postal = postal; }

    public void setCity(String city) { this.city = city; }

    public void setCountry(String country) { this.country = country; }

    public void setVia(String via) { this.via = via; }

    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }

}
