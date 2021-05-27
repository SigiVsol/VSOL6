package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.Json;
import be.vsol.util.Lang;
import be.vsol.util.Str;
import be.vsol.vsol4.Vsol4Client;
import be.vsol.vsol4.Vsol4Organization;
import be.vsol.vsol4.Vsol4User;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.services.Vsol4Service;
import org.json.JSONObject;

import java.util.Vector;

public class LegacyAPI extends API {

    public LegacyAPI(Vsol4Service vsol4Service) {
        super(vsol4Service);
    }

    // LOGIN

    @Override protected HttpResponse authenticate(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        String username = Json.getOrDefault(jsonRequest, "username", "");
        String password = Json.getOrDefault(jsonRequest, "password", "");

        if (vsol4.authenticate(username, password) == null) {
            return HttpResponse.get404(Lang.get("Bad_credentials.", request.getLanguage()));
        } else {
            Vsol4User vsol4User = vsol4.getUser(username);
            Vsol4Organization vsol4Organization = vsol4.getDefaultOrganization(vsol4User);
            return getLogin(vsol4User, vsol4Organization);
        }
    }

    @Override protected HttpResponse restoreLogin(HttpRequest request) {
        JSONObject jsonRequest = request.getBodyAsJSONObject();
        String userId = Json.getOrDefault(jsonRequest, "userId", "");
        String organizationId = Json.getOrDefault(jsonRequest, "organizationId", "");

        Vsol4User vsol4User = vsol4.getById(null, userId, Vsol4User::new);
        Vsol4Organization vsol4Organization = vsol4.getById(null, organizationId, Vsol4Organization::new);
        return getLogin(vsol4User, vsol4Organization);
    }

    private HttpResponse getLogin(Vsol4User vsol4User, Vsol4Organization vsol4Organization) {
        User user = new User(vsol4User);
        Organization organization = new Organization(vsol4Organization);

        return getLogin(user, organization);
    }

    // CLIENTS

    @Override protected HttpResponse getClients(HttpRequest request) {
        String organizationId = Str.cutoff(request.getPath(), "/api/organizations/", "/clients");
        String filter = request.getParameters().getOrDefault("filter", "");

        Vector<Vsol4Client> vsol4Clients = vsol4.getAll(organizationId, filter, Vsol4Client::new);

        System.out.println(request.getPath());
        System.out.println(request.getParameters());
        return null;
    }

    @Override protected HttpResponse getClient(HttpRequest request) {
        return null;
    }

    @Override protected HttpResponse putClient(HttpRequest request) {
        return null;
    }

    @Override protected HttpResponse deleteClient(HttpRequest request) {
        return null;
    }

    @Override protected HttpResponse deleteClients(HttpRequest request) {
        return null;
    }

}
