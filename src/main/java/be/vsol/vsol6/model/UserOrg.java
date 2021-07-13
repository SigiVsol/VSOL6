package be.vsol.vsol6.model;

import be.vsol.vsol6.model.meta.Organization;

public class UserOrg {

    private final User user;
    private final Organization organization;

    // Constructors

    public UserOrg(User user, Organization organization) {
        this.user = user;
        this.organization = organization;
    }

    // Getters

    public User getUser() { return user; }

    public Organization getOrganization() { return organization; }

    public boolean isValid() {
        return user != null && organization != null && !user.getId().isBlank() && !organization.getId().isBlank();
    }

}
