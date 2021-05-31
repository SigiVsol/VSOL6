package be.vsol.vsol6.services;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.tools.Job;
import be.vsol.tools.Minute;
import be.vsol.tools.Service;
import be.vsol.util.Filter;
import be.vsol.util.Json;
import be.vsol.util.Log;
import be.vsol.vsol4.Vsol4Configuration;
import be.vsol.vsol4.Vsol4Organization;
import be.vsol.vsol4.Vsol4Record;
import be.vsol.vsol4.Vsol4User;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.session.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Vector;
import java.util.function.Supplier;

public class Vsol4Service implements Service {

    private final Session session;
    private String token = null;

    public Vsol4Service(Session session) {
        this.session = session;
    }

    @Override public void start() {
        Config.vsol4 vsol4 = session.getConfig().vsol4;

        token = authenticate(vsol4.username, vsol4.password);

        // periodically renew the authentication
        new Job(Minute.ms(vsol4.lifespan), Minute.ms(vsol4.lifespan), () -> token = authenticate(vsol4.username, vsol4.password));
    }

    @Override public void stop() {
        token = null;
    }

    public boolean isConnected() {
        return token != null;
    }

    public String authenticate(String username, String password) {
        JSONObject jsonObject = new JSONObject(); {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        }

        HttpResponse response = getResponse(HttpRequest.Method.POST, null, null, "authenticate", jsonObject);
        if (response == null || !response.isValid()) {
            return null;
        } else {
            return Json.getOrDefault(response.getBodyAsJSONObject(), "id_token", (String) null);
        }
    }

    public Vsol4User getUser(String username) {
        HttpResponse response = getResponse(null, null, "users/" + username);
        return Json.get(getContent(response), Vsol4User::new);
    }

    public Vector<Vsol4Organization> getOrganizations(String username) {
        Vector<Vsol4Organization> result = new Vector<>();

        HttpResponse response = getResponse(null, username, "organizations");

        for (JSONObject jsonOrganization : Json.iterate(getContentArray(response))) {
            result.add(Json.get(jsonOrganization, Vsol4Organization::new));
        }

        return result;
    }

    /** return the default organization associated with this user if defined, otherwise the first one, otherwise null */
    public Vsol4Organization getDefaultOrganization(String username) {
        Vector<Vsol4Organization> organizations = getOrganizations(username);
        if (organizations.isEmpty()) return null;

        Vsol4Organization result = null;
        for (Vsol4Organization organization : organizations) {
            if (organization.isDefaultOrganization()) result = organization;
        }
        if (result == null) {
            result = organizations.firstElement();
        }

        return result;
    }

    public Vsol4Configuration getConfiguration(String organizationId, String username) {
        HttpResponse response = getResponse(organizationId, username, "configurations");

        if (isSuccess(response)) {
            JSONObject jsonObject = getContent(response);
            return Json.get(jsonObject, Vsol4Configuration::new);
        } else {
            return null;
        }
    }

    public <E extends Vsol4Record> E getById(String organizationId, String id, Supplier<E> supplier) {
        E result = supplier.get();

        if (result instanceof Vsol4User) {
            HttpResponse response = getResponse(organizationId, null, "users");
            if (organizationId == null) {
                JSONArray jsonUsers = getContentArray(response);
                for (JSONObject jsonUser : Json.iterate(jsonUsers)) {
                    if (Json.getOrDefault(jsonUser, "id", "").equals(id)) {
                        Json.load(result, jsonUser);
                        break;
                    }
                }
            } else {
                JSONArray jsonOrganizationsUserAliases = getContentArray(response);
                for (JSONObject jsonOrganizationsUserAlias : Json.iterate(jsonOrganizationsUserAliases)) {
                    JSONObject jsonUser = Json.getOrDefault(jsonOrganizationsUserAlias, "user", new JSONObject());
                    if (Json.getOrDefault(jsonUser, "id", "").equals(id)) {
                        Json.load(result, jsonUser);
                        break;
                    }
                }
            }
        } else {
            HttpResponse response = getResponse(organizationId, null, result.getApiName() + "/" + id);
            JSONObject jsonObject = getContent(response);
            Json.load(result, jsonObject);
        }

        return result;
    }

    public <E extends Vsol4Record> Vector<E> getAll(String organizationId, String request, String filter, Supplier<E> supplier) {
        Vector<E> result = new Vector<>();

        HttpResponse response = getResponse(organizationId, null, request);
        JSONArray jsonArray = getContentArray(response);

        for (JSONObject jsonObject : Json.iterate(jsonArray)) {
            E e = supplier.get();
            Json.load(e, jsonObject);

            if (Filter.matches(filter, e.getFilterFields())) {
                result.add(e);
            }
        }

        return result;
    }

    public <E extends Vsol4Record> Vector<E> getAll(String organizationId, String filter, Supplier<E> supplier) {
        return getAll(organizationId, supplier.get().getApiName(), filter, supplier);
    }

    public <E extends Vsol4Record> boolean delete(String organizationId, String id, Supplier<E> supplier) {
        HttpResponse response = getResponse(HttpRequest.Method.DELETE, organizationId, null, supplier.get().getApiName() + "/" + id, null);
        return isSuccess(response);
    }

    public <E extends Vsol4Record> boolean save(String organizationId, E vsolRecord) {
        if (vsolRecord == null) return false;

        JSONObject jsonRecord = Json.get(vsolRecord);
        HttpResponse response;
        if (vsolRecord.getId() == null) {
            response = getResponse(HttpRequest.Method.POST, organizationId, null, vsolRecord.getApiName(), jsonRecord);
            if (isSuccess(response)) {
                vsolRecord.setId(Json.getOrDefault(getContent(response), "id", (String) null));
            }
        } else {
            response = getResponse(HttpRequest.Method.PUT, organizationId, null, vsolRecord.getApiName() + "/" + vsolRecord.getId(), jsonRecord);
        }
        return isSuccess(response);
    }

    private JSONObject getContent(HttpResponse response) {
        return Json.getOrDefault(response.getBodyAsJSONObject(), "content", (JSONObject) null);
    }

    private JSONArray getContentArray(HttpResponse response) {
        return Json.getOrDefault(response.getBodyAsJSONObject(), "content", new JSONArray());
    }

    /** Shorthand for GET response (so without a body) */
    private HttpResponse getResponse(String organizationId, String username, String request) {
        return getResponse(HttpRequest.Method.GET, organizationId, username, request, null);
    }

    private HttpResponse getResponse(HttpRequest.Method method, String organizationId, String username, String request, Object body) {
        String path = "v0/";
        if (organizationId != null) {
            path += "organizations/" + organizationId + "/";
        }
        path += request;

        HttpRequest httpRequest = new HttpRequest(method, path, body);

        if (username != null) {
            httpRequest.getParameters().put("User", username);
        } else if (token != null) {
            httpRequest.getParameters().put("Authorization", "Bearer%20" + token);
        }

        Config.vsol4 vsol4 = session.getConfig().vsol4;

        Log.debug("VSOL4 < " + httpRequest);
        HttpResponse response = Curl.get(vsol4.host, vsol4.port, vsol4.timeout, httpRequest);

        if (!isSuccess(response)) {
            if (response == null) Log.err("VSOL4: empty response");
            else Log.err(response.getBodyAsString());
        }

        return response;
    }

    private boolean isSuccess(HttpResponse response) {
        return response != null && response.isValid() && response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }


}
