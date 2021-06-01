package be.vsol.vsol6.controller.api;

import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.UserOrg;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import be.vsol.vsol6.model.organization.Study;
import javafx.application.Application;

import java.util.Vector;

public abstract class API {

    protected Application application;

    // Constructors

    public API(Application application) {
        this.application = application;
    }

    // Login

    public abstract UserOrg getUserOrg(String username, String password);

    public abstract UserOrg restoreUserOrg(String userId, String organizationId);

    // Users

    public abstract User getUser(String userId);

    // Organizations

    public abstract Vector<Organization> getOrganizations(String username, String filter);

    public abstract Organization getOrganization(String organizationId);

    // Clients

    public abstract Vector<Client> getClients(String organizationId, String filter, String sortField, boolean sortAsc);

    public abstract Client getClient(String organizationId, String clientId);

    public abstract boolean saveClient(String organizationId, Client client);

    public abstract boolean deleteClient(String organizationId, String clientId);

    public abstract int deleteClients(String organizationId, Vector<String> clientIds);

    // Patients

    public abstract Vector<Patient> getPatients(String organizationId, String clientId, String filter, String sortField, boolean sortAsc);

    public abstract Patient getPatient(String organizationId, String patientId);

    public abstract boolean savePatient(String organizationId, Patient patient);

    public abstract boolean deletePatient(String organizationId, String patientId);

    public abstract int deletePatients(String organizationId, Vector<String> patientIds);

    // Entries

    public abstract Vector<Study> getStudies(String organizationId, String patientId, String filter, String sortField, boolean sortAsc);

    public abstract Study getStudy(String organizationId, String studyId);

    public abstract boolean saveStudy(String organizationId, Study study);

    public abstract boolean deleteStudy(String organizationId, String studyId);

    public abstract int deleteStudies(String organizationId, Vector<String> studyIds);


}
