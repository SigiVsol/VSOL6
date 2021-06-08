package be.vsol.vsol6.controller.http;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.vsol6.controller.api.API;
import be.vsol.vsol6.model.config.Config;

import java.util.Map;

public class ServerHandler implements RequestHandler {

    private final Config config;
    private final WebHandler webHandler;
    private final ApiHandler apiHandler;

    public ServerHandler(Config config, Map<String, String> variables, API api) {
        this.config = config;
        this.webHandler = new WebHandler(variables);
        this.apiHandler = new ApiHandler(api);
    }

    @Override public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();

        if (path.matches("/api/.*")) {
            if (config.app.legacy && config.vsol4.forward) {
                return Curl.get(config.vsol4.host, config.vsol4.port, config.vsol4.timeout, request);
            } else {
                return apiHandler.respond(request);
            }
        } else {
            return webHandler.respond(request);
        }
    }

}
