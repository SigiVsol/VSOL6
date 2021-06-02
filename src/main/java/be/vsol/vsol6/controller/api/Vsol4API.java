package be.vsol.vsol6.controller.api;

import be.vsol.vsol4.model.*;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.UserOrg;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import be.vsol.vsol6.model.organization.Study;
import be.vsol.vsol6.services.Vsol4Service;

import java.util.Vector;
import java.util.function.Supplier;

public class Vsol4API extends API {

    private final Vsol4Service vsol4;

    // Constructors

    public Vsol4API(Vsol4Service vsol4Service) {
        super(null);
        this.vsol4 = vsol4Service;
    }

    // Login

    @Override public UserOrg getUserOrg(String username, String password) {
        if (vsol4.authenticate(username, password) == null) {
            return null;
        } else {
            Vsol4User vsol4User = vsol4.getUser(username);
            Vsol4Organization vsol4Organization = vsol4.getDefaultOrganization(username);

            return new UserOrg(new User(vsol4User), new Organization(vsol4Organization));
        }
    }

    @Override public UserOrg restoreUserOrg(String userId, String organizationId) {
        Vsol4User vsol4User = vsol4.getById(null, userId, Vsol4User::new);
        Vsol4Organization vsol4Organization = vsol4.getById(null, organizationId, Vsol4Organization::new);

        if (vsol4User == null || vsol4Organization == null) {
            return null;
        } else {
            return new UserOrg(new User(vsol4User), new Organization(vsol4Organization));
        }
    }

    // Users

    @Override public User getUser(String userId) {
        Vsol4User vsol4User = vsol4.getById(null, userId, Vsol4User::new);
        return vsol4User == null ? null : new User(vsol4User);
    }

    // Organizations

    @Override public Vector<Organization> getOrganizations(String username, String filter) {
        Vector<Vsol4Organization> vsol4Organizations = vsol4.getOrganizations(username, filter);
        Vector<Organization> organizations = new Vector<>();
        for (Vsol4Organization vsol4Organization : vsol4Organizations) {
            organizations.add(new Organization(vsol4Organization));
        }
        return organizations;
    }

    @Override public Organization getOrganization(String organizationId) {
        Vsol4Organization vsol4Organization = vsol4.getById(null, organizationId, Vsol4Organization::new);
        return vsol4Organization == null ? null : new Organization(vsol4Organization);
    }

    // Clients

    @Override public Vector<Client> getClients(String organizationId, String filter, String sortField, boolean sortAsc) {
        Vector<Vsol4Client> vsol4Clients = vsol4.getAll(organizationId, filter, Vsol4Client::new);

        Vector<Client> clients = new Vector<>();
        for (Vsol4Client vsol4Client : vsol4Clients) {
            clients.add(new Client(vsol4Client));
        }
        clients.sort(Client.getComparator(sortField, sortAsc));

        return clients;
    }

    @Override public Client getClient(String organizationId, String clientId) {
        Vsol4Client vsol4Client = vsol4.getById(organizationId, clientId, Vsol4Client::new);
        return vsol4Client == null ? null : new Client(vsol4Client);
    }

    @Override public boolean saveClient(String organizationId, Client client) {
        Vsol4Client vsol4Client = client.getVsol4Client();

        boolean success = vsol4.save(organizationId, vsol4Client);
        if (success) {
            client.setId(vsol4Client.getId());
        }

        return success;
    }

    @Override public boolean deleteClient(String organizationId, String clientId) {
        return vsol4.delete(organizationId, clientId, Vsol4Client::new);
    }

    @Override public int deleteClients(String organizationId, Vector<String> clientIds) {
        return deleteRecords(organizationId, clientIds, Vsol4Client::new);
    }

    // Patients

    @Override public Vector<Patient> getPatients(String organizationId, String clientId, String filter, String sortField, boolean sortAsc) {
        Vector<Vsol4Patient> vsol4Patients;
        if (clientId == null) {
            vsol4Patients = vsol4.getAll(organizationId, filter, Vsol4Patient::new);
        } else {
            vsol4Patients = vsol4.getAll(organizationId, "clients/" + clientId + "/patients", filter, Vsol4Patient::new);
        }

        Vector<Patient> patients = new Vector<>();
        for (Vsol4Patient vsol4Patient : vsol4Patients) {
            patients.add(new Patient(vsol4Patient));
        }
        patients.sort(Patient.getComparator(sortField, sortAsc));

        return patients;
    }

    @Override public Patient getPatient(String organizationId, String patientId) {
        Vsol4Patient vsol4Patient = vsol4.getById(organizationId, patientId, Vsol4Patient::new);
        return vsol4Patient == null ? null : new Patient(vsol4Patient);
    }

    @Override public boolean savePatient(String organizationId, Patient patient) {
        Vsol4Patient vsol4Patient = patient.getVsol4Patient();

        boolean success = vsol4.save(organizationId, vsol4Patient);
        if (success) {
            patient.setId(vsol4Patient.getId());
        }

        return success;
    }

    @Override public boolean deletePatient(String organizationId, String patientId) {
        return vsol4.delete(organizationId, patientId, Vsol4Patient::new);
    }

    @Override public int deletePatients(String organizationId, Vector<String> patientIds) {
        return deleteRecords(organizationId, patientIds, Vsol4Patient::new);
    }

    // Studies

    @Override public Vector<Study> getStudies(String organizationId, String patientId, String filter, String sortField, boolean sortAsc) {
        Vector<Vsol4Study> vsol4Studies;
        if (patientId == null) {
            vsol4Studies = vsol4.getAll(organizationId, filter, Vsol4Study::new);
        } else {
            vsol4Studies = vsol4.getAll(organizationId, "patients/" + patientId + "/studies", filter, Vsol4Study::new);
        }

        Vector<Study> studies = new Vector<>();
        for (Vsol4Study vsol4Study : vsol4Studies) {
            studies.add(new Study(vsol4Study));
        }
        studies.sort(Study.getComparator(sortField, sortAsc));

        return studies;
    }

    @Override public Study getStudy(String organizationId, String studyId) {
        Vsol4Study vsol4Study = vsol4.getById(organizationId, studyId, Vsol4Study::new);
        return vsol4Study == null ? null : new Study(vsol4Study);
    }

    @Override public boolean saveStudy(String organizationId, Study study) {
        Vsol4Study vsol4Study = study.getVsol4Study();

        boolean success = vsol4.save(organizationId, vsol4Study);
        if (success) {
            study.setId(vsol4Study.getId());
        }

        return success;
    }

    @Override public boolean deleteStudy(String organizationId, String studyId) {
        return vsol4.delete(organizationId, studyId, Vsol4Study::new);
    }

    @Override public int deleteStudies(String organizationId, Vector<String> studyIds) {
        return deleteRecords(organizationId, studyIds, Vsol4Study::new);
    }

    // Methods

    private <E extends Vsol4Record> int deleteRecords(String organizationId, Vector<String> ids, Supplier<E> supplier) {
        int total = 0;
        for (String id : ids) {
            if (vsol4.delete(organizationId, id, supplier)) {
                total++;
            }
        }
        return total;
    }

}
