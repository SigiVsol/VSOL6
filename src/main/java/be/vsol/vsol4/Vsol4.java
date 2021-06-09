package be.vsol.vsol4;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.Filter;
import be.vsol.util.Json;
import be.vsol.util.Log;
import be.vsol.vsol4.model.Vsol4Configuration;
import be.vsol.vsol4.model.Vsol4Organization;
import be.vsol.vsol4.model.Vsol4Record;
import be.vsol.vsol4.model.Vsol4User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;
import java.util.function.Supplier;

public class Vsol4 implements Runnable {

    private String host, username, password, token;
    private int port, timeout;

    public Vsol4() { }

    public void start(String host, int port, String username, String password, int timeout) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.timeout = timeout;

        new Thread(this).start();
    }

    @Override public void run() {
        this.token = authenticate(username, password);
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

    public Vector<Vsol4Organization> getOrganizations(String username, String filter) {
        Vector<Vsol4Organization> result = new Vector<>();

        HttpResponse response = getResponse(null, username, "organizations");

        for (JSONObject jsonOrganization : Json.iterate(getContentArray(response))) {
            Vsol4Organization organization = new Vsol4Organization();
            Json.load(organization, jsonOrganization);
            if (Filter.matches(filter, organization.getFilterFields())) {
                result.add(organization);
            }
        }

        return result;
    }

    /** return the default organization associated with this user if defined, otherwise the first one, otherwise null */
    public Vsol4Organization getDefaultOrganization(String username) {
        Vector<Vsol4Organization> organizations = getOrganizations(username, "");
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
        if (id == null) return null;

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
        }
        if (token != null) {
            httpRequest.getParameters().put("Authorization", "Bearer%20" + token);
        }

        Log.debug("VSOL4 < " + httpRequest);
        HttpResponse response = Curl.get(host, port, timeout, httpRequest);

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
