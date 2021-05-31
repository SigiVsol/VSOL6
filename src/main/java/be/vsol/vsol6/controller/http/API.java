package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpRequest.Method;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.*;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Record;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.services.Vsol4Service;
import be.vsol.vsol6.session.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public abstract class API implements RequestHandler {

    protected final Session session;
    protected final Vsol4Service vsol4;

    public API(Session session, Vsol4Service vsol4Service) {
        this.session = session;
        this.vsol4 = vsol4Service;
    }

    @Override public HttpResponse respond(HttpRequest request) {
        Log.out("API < " + request);

        String path = request.getPath();
        Method method = request.getMethod();
        String uid = Uid.regex();

        // LOGIN
        if (method == Method.POST && path.matches("/api/authenticate")) return postAuthentication(request);
        else if (method == Method.POST && path.matches("/api/restoreLogin")) return postRestoreLogin(request);
        // ORGANIZATIONS
        else if (method == Method.GET && path.matches("/api/organizations")) return getOrganizations(request);
        // CLIENTS
        else if (method == Method.GET && path.matches("/api/organizations/" + uid + "/clients")) return getClients(request);
        else if (method == Method.GET && path.matches("/api/organizations/" + uid + "/clients/" + uid)) return getClient(request);
        else if (method == Method.PUT && path.matches("/api/organizations/" + uid + "/clients")) return putClient(request);
        else if (method == Method.DELETE && path.matches("/api/organizations/" + uid + "/clients/" + uid)) return deleteClient(request);
        else if (method == Method.POST && path.matches("/api/organizations/" + uid + "/clients")) return postClientsAction(request);
        // PATIENTS
        else if (method == Method.GET && path.matches("/api/organizations/" + uid + "/patients")) return getPatients(request);
        else if (method == Method.GET && path.matches("/api/organizations/" + uid + "/clients/" + uid + "/patients")) return getPatientsOfClient(request);
        else if (method == Method.GET && path.matches("/api/organizations/" + uid + "/patients/" + uid)) return getPatient(request);
        else if (method == Method.PUT && path.matches("/api/organizations/" + uid + "/patients")) return putPatient(request);
        else if (method == Method.DELETE && path.matches("/api/organizations/" + uid + "/patients/" + uid)) return deletePatient(request);
        else if (method == Method.POST && path.matches("/api/organizations/" + uid + "/patients")) return postPatientsAction(request);
        // STUDIES
        else if (method == Method.GET && path.matches("/api/organizations/" + uid + "/(studies|entries)")) return getStudies(request);
        else if (method == Method.GET && path.matches("/api/organizations/" + uid + "/patients/" + uid + "/(studies|entries)")) return getStudiesOfPatient(request);
        else if (method == Method.DELETE && path.matches("/api/organizations/" + uid + "/(studies|entries)/" + uid)) return deleteStudy(request);
        else if (method == Method.POST && path.matches("/api/organizations/" + uid + "/(studies|entries)")) return postStudyAction(request);

        else return HttpResponse.get404();
    }

    // LOGIN

    /** the request must contain username and password, the response contains JSON with user and organization */
    protected abstract HttpResponse postAuthentication(HttpRequest request);

    /** the request must contain userId and organizationId, the response contains JSON with user and organization */
    protected abstract HttpResponse postRestoreLogin(HttpRequest request);

    protected HttpResponse postLogin(User user, Organization organization) {
        JSONObject jsonResponse = new JSONObject(); {
            jsonResponse.put("user", Json.get(user));
            jsonResponse.put("organization", Json.get(organization));
        }

        return new HttpResponse(jsonResponse);
    }

    protected <E extends Record> HttpResponse getRows(Vector<E> records, int part, int limit) {
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

    // ORGANIZATIONS

    protected abstract HttpResponse getOrganizations(HttpRequest request);

    // CLIENTS

    protected abstract HttpResponse getClients(HttpRequest request);

    protected abstract HttpResponse getClient(HttpRequest request);

    protected abstract HttpResponse putClient(HttpRequest request);

    protected abstract HttpResponse deleteClient(HttpRequest request);

    protected abstract HttpResponse postClientsAction(HttpRequest request);

    // PATIENTS

    protected abstract HttpResponse getPatients(HttpRequest request);

    protected abstract HttpResponse getPatientsOfClient(HttpRequest request);

    protected abstract HttpResponse getPatient(HttpRequest request);

    protected abstract HttpResponse putPatient(HttpRequest request);

    protected abstract HttpResponse deletePatient(HttpRequest request);

    protected abstract HttpResponse postPatientsAction(HttpRequest request);

    // STUDIES

    protected abstract HttpResponse getStudies(HttpRequest request);

    protected abstract HttpResponse getStudiesOfPatient(HttpRequest request);

    protected abstract HttpResponse deleteStudy(HttpRequest request);

    protected abstract HttpResponse postStudyAction(HttpRequest request);

    // Methods

    protected JSONObject getJson(boolean success) {
        JSONObject result = new JSONObject(); {
            result.put("success", success);
        }
        return result;
    }

    protected JSONObject getJson(int success, int total) {
        JSONObject result = new JSONObject(); {
            result.put("success", success);
            result.put("total", total);
        }
        return result;
    }

    protected String getSplit(HttpRequest request, int part) {
        String[] subs = request.getPath().split("/", -1);
        if (subs.length > part) return subs[part];
        else return "";
    }

    protected String getFilter(HttpRequest request) {
        return request.getParameters().getOrDefault("filter", "");
    }

    protected String getUsername(HttpRequest request) {
        return request.getParameters().getOrDefault("username", "");
    }

    protected String getSortField(HttpRequest request) {
        return request.getParameters().getOrDefault("sortField", "");
    }

    protected boolean isSortAsc(HttpRequest request) {
        return Bool.parse(request.getParameters().get("sortAsc"), true);
    }

    protected int getPart(HttpRequest request) {
        return Int.parse(request.getParameters().get("part"), 0);
    }

}
