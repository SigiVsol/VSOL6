package be.vsol.vsol6.model;

import be.vsol.database.db;
import be.vsol.tools.json;
import be.vsol.vsol4.model.Vsol4User;

public class User extends Record {

    @json @db private String username, firstName, lastName, email;

    // Constructors

    public User() { }

    public User(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(Vsol4User vsol4User) {
        super(vsol4User.getId(), vsol4User.getLastOpenedDate());
        username = vsol4User.getUsername();
        firstName = vsol4User.getFirstName();
        lastName = vsol4User.getLastName();
        email = vsol4User.getEmail();
    }

    // Methods

    @Override public String toString() {
        return username;
    }

    // Getters

    public String getUsername() { return username; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

}
