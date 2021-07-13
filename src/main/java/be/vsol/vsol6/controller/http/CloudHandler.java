package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.Json;
import be.vsol.vsol6.model.Query;
import be.vsol.vsol6.model.Update;
import be.vsol.vsol6.model.database.MetaDb;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class CloudHandler implements RequestHandler {

    private final MetaDb metaDb;

    public CloudHandler(MetaDb metaDb) {
        this.metaDb = metaDb;
    }

    @Override
    public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();
        System.out.println("New client with request: " + path);

        if (path.matches("/sync/meta")) {
            System.out.println("sync queries");
            return syncQueries(request);
        }else if(path.matches("/ack/meta")) {
            System.out.println("sync updates");
            return syncUpdates(request);
        }
        else if (path.matches("/sync/client/images")){
            System.out.println("sync images");
            return HttpResponse.get404();
        }

        System.out.println("Unknown request");
        return HttpResponse.get404();
    }

    private HttpResponse syncQueries(HttpRequest request) {
        try{
            JSONObject json = request.getBodyAsJSONObject();
            String computerId = json.getString("computerId");
            String organization = json.getString("organizationId");
            JSONArray jsonQueries = json.getJSONArray("queries");

            Vector<String> queryIds = new Vector<>();
            Set<String> recordIds = new HashSet<>();

            //save incoming UPDATE queries; if INSERT query, execute (once) instantly
            for(int i = 0; i < jsonQueries.length(); i++) {
                Query query = Json.get(jsonQueries.getJSONObject(i), Query::new);

                if (query.getType() == Query.Type.UPDATE) {
                    metaDb.getQueries().save(query);
                }else {
                    metaDb.update(query.getQuery());
                }

                queryIds.add(query.getId());
                recordIds.add(query.getRecordId());
            }

            //execute all (saved) queries involved and add updates
            for(String recordId : recordIds) {
                executeInvolvedQueries(recordId);
                addUpdate(computerId, "organizations", recordId);
            }

            //send response
            return sendResponse(organization, computerId, queryIds);

        }catch(Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.get400("Excepting a JSON with a client, organization and queries");
    }

    private HttpResponse syncUpdates(HttpRequest request) {
        try{
            JSONObject json = request.getBodyAsJSONObject();
            System.out.println(json);
            String computerId = json.getString("computerId");
            String organization = json.getString("organizationId");
            JSONArray jsonUpdates = json.getJSONArray("updateIds");


            for(int i = 0; i < jsonUpdates.length(); i++) {
                Update update = metaDb.getUpdates().get("id=" +  "'" + jsonUpdates.getString(i) + "'");
                System.out.println("Delete update" + jsonUpdates.getString(i));
                update.setDeleted(true);
                metaDb.getUpdates().save(update);
            }

            return new HttpResponse("OK");

        }catch(Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.get400("Excepting a JSON with updateIds");
    }

    private HttpResponse sendResponse(String organization, String computerId, Vector<String> queryIds) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("organizationId", organization);
        jsonResponse.put("queryIds", queryIds);
        jsonResponse = addUpdatesToJson(computerId, jsonResponse);
        return new HttpResponse(jsonResponse);
    }

    private void executeInvolvedQueries(String recordId) {
        Vector<Query> queriesToExecute = metaDb.getQueries().getAll("recordId=" + "'" + recordId + "'", " createdTime ASC");
        for(Query query : queriesToExecute) {
            System.out.println(query.getQuery());
            metaDb.update(query.getQuery());
        }
    }

    private void addUpdate(String client, String tableName, String recordId) {
        Update update = new Update(client, tableName, recordId);
        metaDb.getUpdates().save(update);

    }

    private JSONObject addUpdatesToJson(String computerId, JSONObject jsonObject) {
        Vector<Update> dbUpdates = metaDb.getUpdates().getAll("computerId=" +  "'" + computerId + "'", null);
        Set<String> recordIds = new HashSet<>();
        JSONArray jsonUpdates = new JSONArray();
        for(Update update : dbUpdates) {
            String recordId = update.getRecordId();
            if(!recordIds.contains(recordId)) {
                JSONObject object = new JSONObject();
                object.put("tableName", "organizations");
                object.put("record", Json.get(metaDb.getOrganizations().getById(recordId)));
                jsonUpdates.put(object);
                recordIds.add(recordId);
            }
            jsonObject.append("updateIds", update.getId());
        }
        jsonObject.put("updates", jsonUpdates);
        return jsonObject;
    }
}
