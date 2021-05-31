package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.Json;
import be.vsol.util.Lang;
import be.vsol.util.Log;
import be.vsol.util.Uid;
import be.vsol.vsol4.Vsol4Organization;
import be.vsol.vsol4.Vsol4User;
import be.vsol.vsol6.controller.api.API;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.UserOrg;
import be.vsol.vsol6.model.enums.Language;
import org.json.JSONObject;

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
        else if (method == HttpRequest.Method.POST && path.matches("/api/organizations/" + uid + "/(studies|entries)")) return postStudyAction(request);

        else return HttpResponse.get404();
    }

    private HttpResponse postAuthentication(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        String username = Json.getOrDefault(jsonRequest, "username", "");
        String password = Json.getOrDefault(jsonRequest, "password", "");

        UserOrg userOrg = api.getUserOrg(username, password);
        return postLogin(userOrg, request.getLanguage());
    }

    private HttpResponse postLogin(UserOrg userOrg, Language language) {
        if (userOrg.isValid()) {
            JSONObject jsonResponse = new JSONObject(); {
                jsonResponse.put("user", Json.get(userOrg.getUser()));
                jsonResponse.put("organization", Json.get(userOrg.getOrganization()));
            }
            return new HttpResponse(jsonResponse);
        } else {
            return HttpResponse.get404(Lang.get("Bad_credentials.", language));
        }
    }




}
