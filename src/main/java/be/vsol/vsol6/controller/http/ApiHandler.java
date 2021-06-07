package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.*;
import be.vsol.vsol6.controller.api.API;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Record;
import be.vsol.vsol6.model.UserOrg;
import be.vsol.vsol6.model.enums.Language;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import be.vsol.vsol6.model.organization.Study;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class ApiHandler implements RequestHandler {

    private final API api;

    public ApiHandler(API api) {
        this.api = api;
    }

    @Override public HttpResponse respond(HttpRequest request) {
        Log.out("API < " + request);

        String path = request.getPath();
        HttpRequest.Method method = request.getMethod();
        String uid = Uid.regex();

        // LOGIN
        if (method == HttpRequest.Method.POST && path.matches("/api/authenticate")) return postAuthentication(request);
        else if (method == HttpRequest.Method.POST && path.matches("/api/restoreLogin")) return postRestoreLogin(request);
        // ORGANIZATIONS
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations")) return getOrganizations(request);
        // CLIENTS
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations/" + uid + "/clients")) return getClients(request);
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations/" + uid + "/clients/" + uid)) return getClient(request);
        else if (method == HttpRequest.Method.PUT && path.matches("/api/organizations/" + uid + "/clients")) return putClient(request);
        else if (method == HttpRequest.Method.DELETE && path.matches("/api/organizations/" + uid + "/clients/" + uid)) return deleteClient(request);
        else if (method == HttpRequest.Method.POST && path.matches("/api/organizations/" + uid + "/clients")) return postClientsAction(request);
        // PATIENTS
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations/" + uid + "/patients")) return getPatients(request);
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations/" + uid + "/clients/" + uid + "/patients")) return getPatientsOfClient(request);
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations/" + uid + "/patients/" + uid)) return getPatient(request);
        else if (method == HttpRequest.Method.PUT && path.matches("/api/organizations/" + uid + "/patients")) return putPatient(request);
        else if (method == HttpRequest.Method.DELETE && path.matches("/api/organizations/" + uid + "/patients/" + uid)) return deletePatient(request);
        else if (method == HttpRequest.Method.POST && path.matches("/api/organizations/" + uid + "/patients")) return postPatientsAction(request);
        // STUDIES
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations/" + uid + "/(studies|entries)")) return getStudies(request);
        else if (method == HttpRequest.Method.GET && path.matches("/api/organizations/" + uid + "/patients/" + uid + "/(studies|entries)")) return getStudiesOfPatient(request);
        else if (method == HttpRequest.Method.DELETE && path.matches("/api/organizations/" + uid + "/(studies|entries)/" + uid)) return deleteStudy(request);
        else if (method == HttpRequest.Method.POST && path.matches("/api/organizations/" + uid + "/(studies|entries)")) return postStudiesAction(request);

        else return HttpResponse.get404();
    }

    // Login

    private HttpResponse postAuthentication(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        String username = Json.getOrDefault(jsonRequest, "username", "");
        String password = Json.getOrDefault(jsonRequest, "password", "");

        UserOrg userOrg = api.getUserOrg(username, password);
        return postLogin(userOrg, request.getLanguage());
    }

    private HttpResponse postRestoreLogin(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        String userId = Json.getOrDefault(jsonRequest, "userId", "");
        String organizationId = Json.getOrDefault(jsonRequest, "organizationId", "");

        UserOrg userOrg = api.restoreUserOrg(userId, organizationId);
        return postLogin(userOrg, request.getLanguage());
    }

    private HttpResponse postLogin(UserOrg userOrg, Language language) {
        if (userOrg != null && userOrg.isValid()) {
            JSONObject jsonResponse = new JSONObject(); {
                jsonResponse.put("user", Json.get(userOrg.getUser()));
                jsonResponse.put("organization", Json.get(userOrg.getOrganization()));
            }
            return new HttpResponse(jsonResponse);
        } else {
            return HttpResponse.get404(Lang.get("Bad_credentials.", language));
        }
    }

    // Organizations

    private HttpResponse getOrganizations(HttpRequest request) {
        Vector<Organization> organizations = api.getOrganizations(getUsername(request), getFilter(request));
        return getRows(organizations, getPart(request));
    }

    // Clients

    private HttpResponse getClients(HttpRequest request) {
        Vector<Client> clients = api.getClients(getSplit(request, 3), getFilter(request), getSortField(request), isSortAsc(request));
        return getRows(clients, getPart(request));
    }

    private HttpResponse getClient(HttpRequest request) {
        Client client = api.getClient(getSplit(request, 3), getSplit(request, 5));
        return getResponse(client);
    }

    private HttpResponse putClient(HttpRequest request) {
        Client client = Json.get(request.getBodyAsJSONObject(), Client::new);
        api.saveClient(getSplit(request, 3), client);
        return getResponse(client);
    }

    private HttpResponse deleteClient(HttpRequest request) {
        boolean success = api.deleteClient(getSplit(request, 3), getSplit(request, 5));
        return getResponse(success);
    }

    private HttpResponse postClientsAction(HttpRequest request) {
        String action = request.getParameters().get("action");
        int total = 0;

        if (action.equals("delete")) {
            total = api.deleteClients(getSplit(request, 3), getIds(request));
        }
        return getResponse(total);
    }

    // Patients

    private HttpResponse getPatients(HttpRequest request) {
        Vector<Patient> patients = api.getPatients(getSplit(request, 3), null, getFilter(request), getSortField(request), isSortAsc(request));
        return getRows(patients, getPart(request));
    }

    private HttpResponse getPatientsOfClient(HttpRequest request) {
        Vector<Patient> patients = api.getPatients(getSplit(request, 3), getSplit(request, 5), getFilter(request), getSortField(request), isSortAsc(request));
        return getRows(patients, getPart(request));
    }

    private HttpResponse getPatient(HttpRequest request) {
        Patient patient = api.getPatient(getSplit(request, 3), getSplit(request, 5));
        return getResponse(patient);
    }

    private HttpResponse putPatient(HttpRequest request) {
        Patient patient = Json.get(request.getBodyAsJSONObject(), Patient::new);
        api.savePatient(getSplit(request, 3), patient);
        return getResponse(patient);
    }

    private HttpResponse deletePatient(HttpRequest request) {
        boolean success = api.deletePatient(getSplit(request, 3), getSplit(request, 5));
        return getResponse(success);
    }

    private HttpResponse postPatientsAction(HttpRequest request) {
        String action = request.getParameters().get("action");
        int total = 0;

        if (action.equals("delete")) {
            total = api.deletePatients(getSplit(request, 3), getIds(request));
        }
        return getResponse(total);
    }

    // Studies

    private HttpResponse getStudies(HttpRequest request) {
        Vector<Study> studies = api.getStudies(getSplit(request, 3), null, getFilter(request), getSortField(request), isSortAsc(request));
        return getRows(studies, getPart(request));
    }

    private HttpResponse getStudiesOfPatient(HttpRequest request) {
        Vector<Study> studies = api.getStudies(getSplit(request, 3), getSplit(request, 5), getFilter(request), getSortField(request), isSortAsc(request));
        return getRows(studies, getPart(request));
    }

    private HttpResponse deleteStudy(HttpRequest request) {
        boolean success = api.deleteStudy(getSplit(request, 3), getSplit(request, 5));
        return getResponse(success);
    }

    private HttpResponse postStudiesAction(HttpRequest request) {
        String action = request.getParameters().get("action");
        int total = 0;

        if (action.equals("delete")) {
            total = api.deleteStudies(getSplit(request, 3), getIds(request));
        }
        return getResponse(total);
    }

    // Methods

    private String getSplit(HttpRequest request, int part) {
        String[] subs = request.getPath().split("/", -1);
        if (subs.length > part) return subs[part];
        else return "";
    }

    private String getFilter(HttpRequest request) {
        return request.getParameters().getOrDefault("filter", "");
    }

    private String getUsername(HttpRequest request) {
        return request.getParameters().getOrDefault("username", "");
    }

    private String getSortField(HttpRequest request) {
        return request.getParameters().getOrDefault("sortField", "");
    }

    private boolean isSortAsc(HttpRequest request) {
        return Bool.parse(request.getParameters().get("sortAsc"), true);
    }

    private int getPart(HttpRequest request) {
        return Int.parse(request.getParameters().get("part"), 0);
    }

    private Vector<String> getIds(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        JSONArray jsonIds = Json.getOrDefault(jsonRequest, "ids", new JSONArray());
        return Json.getStringVector(jsonIds);
    }

    private <E extends Record> HttpResponse getRows(Vector<E> records, int part) {
//        int limit = sessionOld.getConfig().server.rowLimit;
        int limit = 20;

        JSONObject jsonResponse = new JSONObject(); {
            JSONArray jsonRows = new JSONArray(); {
                for (int i = part * limit; i < (part + 1) * limit && i < records.size(); i++) {
                    jsonRows.put(Json.get(records.get(i)));
                }
            } jsonResponse.put("rows", jsonRows);

            jsonResponse.put("availableRows", records.size());
        }

        return new HttpResponse(jsonResponse);
    }

    private <E extends Record> HttpResponse getResponse(E record) {
        return new HttpResponse(Json.get(record));
    }

    private HttpResponse getResponse(boolean success) {
        JSONObject result = new JSONObject(); {
            result.put("success", success);
        }
        return new HttpResponse(result);
    }

    private HttpResponse getResponse(int total) {
        JSONObject result = new JSONObject(); {
            result.put("total", total);
        }
        return new HttpResponse(result);
    }



}
