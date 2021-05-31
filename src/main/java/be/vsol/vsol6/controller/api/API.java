package be.vsol.vsol6.controller.api;

import be.vsol.vsol6.model.UserOrg;
import be.vsol.vsol6.model.organization.Client;

import java.util.Vector;

public abstract class API {

    // Constructors

    public API() {

    }

    // Abstract Methods

    public abstract UserOrg getUserOrg(String username, String password);

    public abstract UserOrg restoreUserOrg(String userId, String organizationId);

    public abstract Vector<Client> getClients(String organizationId, String filter, String sortField, boolean sortAsc, int part);




}
