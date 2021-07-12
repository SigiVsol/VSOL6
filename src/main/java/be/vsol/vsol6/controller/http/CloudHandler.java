package be.vsol.vsol6.controller.http;

import be.vsol.database.model.DbTable;
import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.Json;
import be.vsol.vsol6.model.Organization;
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

        if (path.matches("/sync/client/data")) {
            System.out.println("New client with request: " + path);
            try{
                JSONObject json = request.getBodyAsJSONObject();
                String client = json.getString("computerId");
                String organization = json.getString("organizationId");
                JSONArray jsonQueries = json.getJSONArray("queries");

                Vector<Query> queries = new Vector<>();
                Vector<String> queryIds = new Vector<>();
                Set<String> recordIds = new HashSet<String>();

                //save queries
                for(int i = 0; i < jsonQueries.length(); i++) {
                    Query query = Json.get(jsonQueries.getJSONObject(i), Query::new);
                    queryIds.add(query.getId());
                    recordIds.add(query.getRecordId());
                    queries.add(query);
                    metaDb.getQueries().save(query);
                }

                //execute queries
                for(String recordId : recordIds) {
                    Vector<Query> queriesToExecute = metaDb.getQueries().getAll("recordId=" + "'" + recordId + "' AND type='UPDATE'", " createdTime ASC");
                    for(Query query : queriesToExecute) {
                        System.out.println(query.getQuery());
                        metaDb.update(query.getQuery());
                        metaDb.getQueries().save(query);
                    }
                }

                //make changes as a test
//                Vector<Organization> orgs = metaDb.getOrganizations().getAll();
//                for(Organization org : orgs)
//                {
//                    org.setName("Pieter");
//                    org.setDescription("Pieter was hier :-)");
//                    metaDb.getOrganizations().save(org);
//                }


                //save updates
                for(Query query : queries) {
                    if(metaDb.getUpdates().get(" tableName='" + query.getTableName() + "' AND " + " recordId='" + query.getRecordId() + "'") == null) {
                        Update update = new Update(client, query.getTableName(), query.getRecordId());
                        metaDb.getUpdates().save(update);
                    }
                }

                //get updates
                Vector<Update> updates = metaDb.getUpdates().getAll("computerId=" +  "'" + client + "'", null);
                JSONArray updateArray = new JSONArray();
                for(Update update : updates) {
                    JSONObject object = new JSONObject();
                    object.put("tableName", "organizations");
                    object.put("record", Json.get(metaDb.getOrganizations().getById(update.getRecordId())));
                    updateArray.put(object);
                    update.setDeleted(true);
                    metaDb.getUpdates().save(update);
                }

                //send response
                return sendResponse(organization, queryIds, updateArray);

            }catch(Exception e) {
                e.printStackTrace();
                HttpResponse.get404("Unknown request: expect a json object with a client, organization and a list of queries.");
            }

        } else if (path.matches("/sync/client/images")){
            return HttpResponse.get404();
        }
        return HttpResponse.get404();
    }

    public HttpResponse sendResponse(String organization, Vector<String> queryIds, JSONArray updates) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("organizationId", organization);
        jsonResponse.put("queryIds", queryIds);
        jsonResponse.put("updates", updates);
        return new HttpResponse(jsonResponse);
    }
}
