package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.*;
import be.vsol.vsol4.*;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import be.vsol.vsol6.model.organization.Study;
import be.vsol.vsol6.services.Vsol4Service;
import be.vsol.vsol6.session.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;
import java.util.function.Supplier;

public class LegacyAPI extends API {

    public LegacyAPI(Session session, Vsol4Service vsol4Service) {
        super(session, vsol4Service);
    }

    // LOGIN

    @Override protected HttpResponse postAuthentication(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        String username = Json.getOrDefault(jsonRequest, "username", "");
        String password = Json.getOrDefault(jsonRequest, "password", "");

        if (vsol4.authenticate(username, password) == null) {
            return HttpResponse.get404(Lang.get("Bad_credentials.", request.getLanguage()));
        } else {
            Vsol4User vsol4User = vsol4.getUser(username);
            Vsol4Organization vsol4Organization = vsol4.getDefaultOrganization(username);
            return postLogin(vsol4User, vsol4Organization);
        }
    }

    @Override protected HttpResponse postRestoreLogin(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        String userId = Json.getOrDefault(jsonRequest, "userId", "");
        String organizationId = Json.getOrDefault(jsonRequest, "organizationId", "");

        Vsol4User vsol4User = vsol4.getById(null, userId, Vsol4User::new);
        Vsol4Organization vsol4Organization = vsol4.getById(null, organizationId, Vsol4Organization::new);
        return postLogin(vsol4User, vsol4Organization);
    }

    private HttpResponse postLogin(Vsol4User vsol4User, Vsol4Organization vsol4Organization) {
        User user = new User(vsol4User);
        Organization organization = new Organization(vsol4Organization);

        return postLogin(user, organization);
    }

    // CLIENTS

    @Override protected HttpResponse getClients(HttpRequest request) {
        String organizationId = getSplit(request, 3);

        Vector<Vsol4Client> vsol4Clients = vsol4.getAll(organizationId, getFilter(request), Vsol4Client::new);

        Vector<Client> clients = new Vector<>();
        for (Vsol4Client vsol4Client : vsol4Clients) {
            clients.add(new Client(vsol4Client));
        }
        clients.sort(Client.getComparator(getSortField(request), isSortAsc(request)));

        return getRows(clients, getPart(request), session.getConfig().vsol4.limit);
    }

    @Override protected HttpResponse getClient(HttpRequest request) {
        String organizationId = getSplit(request, 3);
        String clientId = getSplit(request, 5);

        Vsol4Client vsol4Client = vsol4.getById(organizationId, clientId, Vsol4Client::new);
        Client client = new Client(vsol4Client);

        return new HttpResponse(Json.get(client));
    }

    @Override protected HttpResponse putClient(HttpRequest request) {
        String organizationId = getSplit(request, 3);
        Client client = Json.get(request.getBodyAsJSONObject(), Client::new);
        Vsol4Client vsol4Client = client.getVsol4Client();

        boolean success = vsol4.save(organizationId, vsol4Client);
        if (success) {
            client.setId(vsol4Client.getId());
            return new HttpResponse(Json.get(client));
        } else {
            return new HttpResponse(getJson(false));
        }
    }

    @Override protected HttpResponse deleteClient(HttpRequest request) {
        return deleteRecords(request, Vsol4Client::new);
    }

    @Override protected HttpResponse postClientsAction(HttpRequest request) {
        return postRecordsAction(request, Vsol4Client::new);
    }

    // PATIENTS

    @Override protected HttpResponse getPatients(HttpRequest request) {
        String organizationId = getSplit(request, 3);

        Vector<Vsol4Patient> vsol4Patients = vsol4.getAll(organizationId, getFilter(request), Vsol4Patient::new);
        return getPatients(request, vsol4Patients);
    }

    @Override protected HttpResponse getPatientsOfClient(HttpRequest request) {
        String organizationId = getSplit(request, 3);
        String clientId = getSplit(request, 5);

        Vector<Vsol4Patient> vsol4Patients = vsol4.getAll(organizationId, "clients/" + clientId + "/patients", getFilter(request), Vsol4Patient::new);
        return getPatients(request, vsol4Patients);
    }

    private HttpResponse getPatients(HttpRequest request, Vector<Vsol4Patient> vsol4Patients) {
        Vector<Patient> patients = new Vector<>();
        for (Vsol4Patient vsol4Patient : vsol4Patients) {
            patients.add(new Patient(vsol4Patient));
        }
        patients.sort(Patient.getComparator(getSortField(request), isSortAsc(request)));

        return getRows(patients, getPart(request), session.getConfig().vsol4.limit);
    }

    @Override protected HttpResponse getPatient(HttpRequest request) {
        String organizationId = getSplit(request, 3);
        String patientId = getSplit(request, 5);

        Vsol4Patient vsol4Patient = vsol4.getById(organizationId, patientId, Vsol4Patient::new);
        Patient patient = new Patient(vsol4Patient);

        return new HttpResponse(Json.get(patient));
    }

    @Override protected HttpResponse putPatient(HttpRequest request) {
        String organizationId = getSplit(request, 3);
        Patient patient = Json.get(request.getBodyAsJSONObject(), Patient::new);
        Vsol4Patient vsol4Patient = patient.getVsol4Patient();

        boolean success = vsol4.save(organizationId, vsol4Patient);
        if (success) {
            patient.setId(vsol4Patient.getId());
            return new HttpResponse(Json.get(patient));
        } else {
            return new HttpResponse(getJson(false));
        }
    }

    @Override protected HttpResponse deletePatient(HttpRequest request) {
        return deleteRecords(request, Vsol4Patient::new);
    }

    @Override protected HttpResponse postPatientsAction(HttpRequest request) {
        return postRecordsAction(request, Vsol4Patient::new);
    }

    // STUDIES

    @Override protected HttpResponse getStudies(HttpRequest request) {
        String organizationId = getSplit(request, 3);

        Vector<Vsol4Study> vsol4Studies = vsol4.getAll(organizationId, getFilter(request), Vsol4Study::new);
        return getStudies(request, vsol4Studies);
    }

    @Override protected HttpResponse getStudiesOfPatient(HttpRequest request) {
        String organizationId = getSplit(request, 3);
        String patientId = getSplit(request, 5);

        Vector<Vsol4Study> vsol4Studies = vsol4.getAll(organizationId, "patients/" + patientId + "/studies", getFilter(request), Vsol4Study::new);
        return getStudies(request, vsol4Studies);
    }

    private HttpResponse getStudies(HttpRequest request, Vector<Vsol4Study> vsol4Studies) {
        Vector<Study> studies = new Vector<>();
        for (Vsol4Study vsol4Study : vsol4Studies) {
            studies.add(new Study(vsol4Study));
        }
        studies.sort(Study.getComparator(getSortField(request), isSortAsc(request)));

        return getRows(studies, getPart(request), session.getConfig().vsol4.limit);
    }

    @Override protected HttpResponse deleteStudy(HttpRequest request) {
        return null;
    }

    @Override protected HttpResponse postStudyAction(HttpRequest request) {
        return null;
    }


    // REPORTS

    // ENTRIES

    // Methods

    private <E extends Vsol4Record> HttpResponse deleteRecords(HttpRequest request, Supplier<E> supplier) {
        String organizationId = getSplit(request, 3);
        String id = getSplit(request, 5);

        boolean success = vsol4.delete(organizationId, id, supplier);
        return new HttpResponse(getJson(success));
    }

    private <E extends Vsol4Record> HttpResponse postRecordsAction(HttpRequest request, Supplier<E> supplier) {
        String organizationId = getSplit(request, 3);
        JSONObject jsonRequest = request.getBodyAsJSONObject();

        String action = Json.getOrDefault(jsonRequest, "action", "");
        JSONArray jsonIds = Json.getOrDefault(jsonRequest, "ids", new JSONArray());
        Vector<String> ids = Json.getStringVector(jsonIds);

        int success = 0;
        if (action.equals("delete")) {
            for (String id : ids) {
                if (vsol4.delete(organizationId, id, supplier)) {
                    success++;
                }
            }
        }

        return new HttpResponse(getJson(success, ids.size()));
    }

}
