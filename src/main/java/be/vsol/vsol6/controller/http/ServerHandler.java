package be.vsol.vsol6.controller.http;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.vsol6.controller.backend.DataStorage;
import be.vsol.vsol6.controller.backend.DicomStorage;
import be.vsol.vsol6.model.config.Config;

import java.util.Map;

public class ServerHandler implements RequestHandler {

    private final Config config;
    private final WebHandler webHandler;
    private final API api;

    public ServerHandler(Config config, Map<String, String> variables, DataStorage dataStorage, DicomStorage dicomStorage) {
        this.config = config;
        this.webHandler = new WebHandler(variables);
        this.api = new API(dataStorage, dicomStorage);
    }

    @Override public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();

        if (path.matches("/api/.*")) {
            if (config.bridge.active) {
                return Curl.get(config.bridge.host, config.bridge.port, config.bridge.timeout, request);
            } else {
                return api.respond(request);
            }
        } else {
            return webHandler.respond(request);
        }
    }

}
