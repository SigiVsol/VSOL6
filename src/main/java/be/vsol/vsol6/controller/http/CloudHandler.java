package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.vsol6.model.Update;
import be.vsol.vsol6.model.database.Db;
import be.vsol.vsol6.model.database.MetaDb;
import be.vsol.vsol6.model.database.OrganizationDb;
import be.vsol.vsol6.model.database.SyncDb;
import be.vsol.vsol6.model.meta.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class CloudHandler implements RequestHandler {

    private final Db database;

    public CloudHandler(Db database) {
        this.database = database;
    }

    @Override public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();
        System.out.println("New client with request: " + path);

        if (path.matches("/sync")) {
            System.out.println("sync queries");
            return sync(request);
        } else if (path.matches("/ack")) {
            System.out.println("sync updates");
            return ack(request);
        }
        else if (path.matches("/sync/client/images")) {
            // TODO
            System.out.println("sync images");
            return HttpResponse.get404();
        }

        System.out.println("Unknown request");
        return HttpResponse.get404();
    }

    private HttpResponse sync(HttpRequest request) {
        try {
            MetaDb metaDb = database.getMetaDb();
            //Load needed data from request
            JSONObject json = request.getBodyAsJSONObject();
            System.out.println("REQUEST" + json);
            String computerId = json.getString("computerId");
            JSONArray data = json.getJSONArray("data");
            JSONObject meta = getFromJsonData(data, null);

            JSONObject jsonResponse = new JSONObject();

            //Check meta updates for all organizations on computer
            JSONObject metaResponse = new JSONObject();
            metaResponse.put("organizationId", (String) null);
            HashMap<String, String> updateRecords = new HashMap<>();
            Vector<String> syncIds = metaDb.getMetaUpdates();
            metaResponse.put("queryIds", metaDb.syncQueries(ge,meta.getJSONArray("queries")));
            metaDb.addUpdatesToJson(computerId,metaResponse);

            //Check organizations updates
            Vector<Network> networks = metaDb.getNetworks().getAll("computerId='" + computerId + "'", null);
            for (Network network : networks) {
                OrganizationDb organizationDb = database.getOrganizationDb(network.getOrganizationId());
                JSONObject organizationResponse = new JSONObject();
                organizationResponse.put("id", network.getOrganizationId());
                if (network.isInitialized()) {
                    JSONObject organization = getFromJsonData(data, network.getOrganizationId());
                    syncIds = metaDb.getUpdateOnOrganisation(computerId, network.getOrganizationId());
                    updateRecords = new HashMap<>();
                    organizationResponse.put("queryIds", organizationDb.syncQueries(syncIds, organization.getJSONArray("queries"),updateRecords));
                    organizationDb.addUpdatesToJson(computerId, organizationResponse);
                }else{
                    metaDb.addAllToJson(computerId, network.getOrganizationId(), metaResponse.getJSONArray("records"));
                    organizationDb.addAllToJson(organizationResponse);
                }
                jsonResponse.append("organizations", organizationResponse);
            }
            jsonResponse.append("organizations", metaResponse);
            System.out.println("RESPONSE" + jsonResponse);
            return new HttpResponse(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.get400("Excepting a JSON with a client, organization and queries");
    }

    private HttpResponse ack(HttpRequest request) {
        try{
            MetaDb metaDb = database.getMetaDb();
            //Load needed data from request
            JSONObject json = request.getBodyAsJSONObject();
            System.out.println("REQUEST" + json);
            String computerId = json.getString("computerId");
            JSONArray data = json.getJSONArray("data");
            JSONObject meta = getFromJsonData(data, null);

            for(int i = 0; i < data.length(); i++) {
                JSONObject organization = data.getJSONObject(i);
                String organizationId = organization.getString("organizationId");
                JSONArray updates = organization.getJSONArray("updateIds");
                SyncDb syncDb;
                //select correct database
                if(organizationId == null) {
                    syncDb = metaDb;
                }else{
                    syncDb = database.getOrganizationDb(organizationId);
                    Network network = metaDb.getNetworks().get("computerId='" + computerId + "' AND organizationId='" + organizationId + "'", null);
                    if(!network.isInitialized()) {
                        network.setInitialized();
                        metaDb.getNetworks().save(network);
                    }
                }
                //save updates
                for(int j = 0; i < updates.length(); j++) {
                    Update update = syncDb.getUpdates().get("id=" +  "'" + updates.getString(j) + "'");
                    update.setDeleted(true);
                    metaDb.getUpdates().save(update);
                }
            }

            System.out.println("RESPONSE:" + "OK");
            return new HttpResponse("OK");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.get400("Excepting a JSON with updateIds");
    }

    private JSONObject getFromJsonData(JSONArray organizations, String organizationId) {
        JSONObject selected = new JSONObject();

        for(int i = 0; i < organizations.length();i++) {
            JSONObject organization = organizations.getJSONObject(i);
            if(organization.getString("id").equals(organizationId)) {
                selected = organization;
                break;
            }
        }
        return selected;
    }
}
