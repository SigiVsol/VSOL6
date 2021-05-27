package be.vsol.vsol4;

import be.vsol.tools.json;

public class Vsol4User extends Vsol4Record {

    @json private String username, firstName, lastName, email;

    // Constructors

    public Vsol4User() {
        super("users");
    }

    // Methods


    @Override public String[] getFilterFields() {
        return new String[] { firstName, lastName, email };
    }

    @Override public String toString() {
        return username;
    }

    // Getters

    public String getUsername() { return username; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

}
