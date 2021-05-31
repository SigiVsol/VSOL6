package be.vsol.vsol6.controller.api;

import be.vsol.vsol6.model.UserOrg;
import be.vsol.vsol6.services.Vsol4Service;

public class Vsol4API extends API {

    private final Vsol4Service vsol4;

    // Constructors

    public Vsol4API(Vsol4Service vsol4Service) {
        super();
        this.vsol4 = vsol4Service;
    }

    // Methods

    @Override protected UserOrg getUserOrg(String username, String password) {
        return null;
    }

    @Override protected UserOrg restoreUserOrg(String userId, String organizationId) {
        return null;
    }

}
