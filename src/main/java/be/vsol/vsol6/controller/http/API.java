package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.Json;
import be.vsol.util.Log;

import be.vsol.http.HttpRequest.Method;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.services.Vsol4Service;
import org.json.JSONObject;

public abstract class API implements RequestHandler {

    protected final Vsol4Service vsol4;

    public API(Vsol4Service vsol4Service) {
        vsol4 = vsol4Service;
    }

    @Override public HttpResponse respond(HttpRequest request) {
        Log.out("API < " + request);

        String path = request.getPath();
        Method method = request.getMethod();

        switch (method) {
            case GET -> {
                if (path.matches("/api/organizations/.*/clients")) return getClients(request);
                else if (path.matches("/api/organizations/.*/clients/.*")) return getClient(request);
            }
            case POST -> {
                if (path.matches("/api/authenticate")) return authenticate(request);
                else if (path.matches("/api/restoreLogin")) return restoreLogin(request);
            }
            case PUT -> {
                if (path.matches("/api/organizations/.*/clients")) return putClient(request);
            }
            case DELETE -> {
                if (path.matches("/api/organizations/.*/clients/.*")) return deleteClient(request);
                else if (path.matches("/api/organizations/.*/clients")) return deleteClients(request);
            }
        }

        return HttpResponse.get404();
    }

    // LOGIN

    /** the request must contain username and password, the response contains JSON with user and organization */
    protected abstract HttpResponse authenticate(HttpRequest request);

    /** the request must contain userId and organizationId, the response contains JSON with user and organization */
    protected abstract HttpResponse restoreLogin(HttpRequest request);

    protected HttpResponse getLogin(User user, Organization organization) {
        JSONObject jsonResponse = new JSONObject(); {
            jsonResponse.put("user", Json.get(user));
            jsonResponse.put("organization", Json.get(organization));
        }

        return new HttpResponse(jsonResponse);
    }

    // CLIENTS

    protected abstract HttpResponse getClients(HttpRequest request);

    protected abstract HttpResponse getClient(HttpRequest request);

    /** the request must contain a client in JSON format */
    protected abstract HttpResponse putClient(HttpRequest request);

    protected abstract HttpResponse deleteClient(HttpRequest request);

    /** the request must contain a JSON with all client ids to be deleted */
    protected abstract HttpResponse deleteClients(HttpRequest request);

}
