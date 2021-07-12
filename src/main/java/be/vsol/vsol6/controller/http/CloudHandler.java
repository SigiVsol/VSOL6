package be.vsol.vsol6.controller.http;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.vsol6.controller.backend.DataStorage;
import be.vsol.vsol6.controller.backend.DicomStorage;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Query;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CloudHandler implements RequestHandler {

    private final DataStorage dataStorage;
    private final DicomStorage dicomStorage;

    public CloudHandler(DataStorage dataStorage, DicomStorage dicomStorage) {
        this.dataStorage = dataStorage;
        this.dicomStorage = dicomStorage;
    }

    @Override
    public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();

        if (path.matches("/sync/client/data")) {
            System.out.println("New client with request: " + path);
            JSONObject json = request.getBodyAsJSONObject();
            String client = json.getString("client");
            String organization = json.getString("organization");
            JSONArray queries = json.getJSONArray("queries");
            System.out.println("Client: " + client);
            System.out.println("Organization" + organization);
            System.out.println("Queries" + queries.toString());

            for(int i = 0; i < queries.length(); i++) {
                //Query query = Json.get(queries.getJSONObject(i), Query::new)
                //dataStorage.saveQuery(organization, query);
            }
            //save queries to query table
            //for each record, check for other updates in the query table, execute queries sorted on time on the DB again
            //check which other computers there are for this organization
            //fill update table with updates

            return sendResponse(organization);
        } else if (path.matches("/sync/client/images")){
            return HttpResponse.get404();
        }
        return HttpResponse.get404();
    }

    public HttpResponse sendResponse(String organization) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("organization", organization);
        //json.put("queries");
        return new HttpResponse(jsonResponse);
    }
}
