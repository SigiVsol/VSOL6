package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.vsol6.model.database.Db;

public class CloudHandler implements RequestHandler {

    private final Db db;

    public CloudHandler(Db db) {
        this.db = db;
    }

    @Override public HttpResponse respond(HttpRequest request) {
        return null;
    }

}
