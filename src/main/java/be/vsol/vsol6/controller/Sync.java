package be.vsol.vsol6.controller;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.Json;
import be.vsol.vsol6.model.database.OrganizationDb;
import be.vsol.vsol6.model.meta.Organization;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Sync {

    private final Ctrl ctrl;
    private String cloudHost;
    private int cloudPort;

    public Sync(Ctrl ctrl) {
        this.ctrl = ctrl;
    }

    public void start(String cloudHost, int cloudPort, int interval, String unit) {
        this.cloudHost = cloudHost;
        this.cloudPort = cloudPort;

//        Task.run("sync", 0, Time.ms(interval, unit), () -> System.out.println("sync: " + Time.ms(interval, unit) + "ms"));
//        Task.run("sync", 0, Time.ms(interval, unit), this::sync);
    }

    public void sync() {
        try {
            // /sync
            JSONObject syncRequest = getRequestJson(getQueries());
            System.out.println("Request: " + syncRequest);
            HttpResponse httpResponse = send("/sync", syncRequest);

            // response
            JSONObject response = httpResponse.getBodyAsJSONObject();
            System.out.println("Response: " + response);
            JSONArray data = response.getJSONArray("data");

            updateDbs(data);

            // /ack
            JSONArray updateIds = getUpdateIds(data);
            JSONObject ackRequest = getRequestJson(updateIds);
            System.out.println("Ack: " + ackRequest);
            send("/ack", ackRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getRequestJson(JSONArray data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("computerId", "comp3");
        jsonObject.put("data", data);
        return jsonObject;
    }

    private JSONArray getQueries() {
        JSONArray data = new JSONArray();

        JSONObject syncMeta = new JSONObject();
        syncMeta.put("organizationId", JSONObject.NULL);
        syncMeta.put("queries", ctrl.getDb().getMetaDb().getQueries().getAll());
        data.put(syncMeta);

        for (Organization organization : ctrl.getDb().getMetaDb().getOrganizations().getAll()) {
            String organizationId = organization.getId();
            JSONObject syncOrg = new JSONObject();
            syncOrg.put("organizationId", organizationId);
            syncOrg.put("queries", getOrCreateOrgDb(organizationId).getQueries().getAll());
            data.put(syncOrg);
        }

        return data;
    }

    private HttpResponse send(String path, JSONObject jsonObject) {
        HttpRequest httpRequest = new HttpRequest(HttpRequest.Method.POST, path, jsonObject);
        return Curl.get(cloudHost, cloudPort, 1000, httpRequest);
    }

    private void updateDbs(JSONArray data) {
        for (int i = 0; i < data.length(); i++) {
            String organizationId = data.getJSONObject(i).optString("organizationId");

            if (organizationId.equals("")) {
                JSONArray queryIds = data.getJSONObject(i).getJSONArray("queryIds");
                JSONArray records = data.getJSONObject(i).getJSONArray("records");
                ctrl.getDb().getMetaDb().deleteQueries(queryIds);
                ctrl.getDb().getMetaDb().updateRecords(records);
                break;
            }
        }
        for (int i = 0; i < data.length(); i++) {
            String organizationId = data.getJSONObject(i).optString("organizationId");

            if (!organizationId.equals("")) {
                JSONArray queryIds = data.getJSONObject(i).getJSONArray("queryIds");
                JSONArray records = data.getJSONObject(i).getJSONArray("records");
                OrganizationDb organizationDb = getOrCreateOrgDb(organizationId);
                organizationDb.deleteQueries(queryIds);
                organizationDb.updateRecords(records);
            }
        }
    }

    private OrganizationDb getOrCreateOrgDb(String organizationId) {
        OrganizationDb organizationDb = ctrl.getDb().getOrganizationDb(organizationId);
        if (organizationDb == null) {
            Organization organization = ctrl.getDb().getMetaDb().getOrganizations().getById(organizationId);
            ctrl.getDb().addOrganizationDb(organization);
            organizationDb = ctrl.getDb().getOrganizationDb(organizationId);
        }
        return organizationDb;
    }

    private JSONArray getUpdateIds(JSONArray data) {
        for (int i = 0; i < data.length(); i++) {
            JSONObject syncData = data.getJSONObject(i);
            syncData.remove("queryIds");
            syncData.remove("records");
        }
        return data;
    }
}
