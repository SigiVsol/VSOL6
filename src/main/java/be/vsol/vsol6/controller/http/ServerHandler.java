package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.img.Png;
import be.vsol.tools.Css;
import be.vsol.tools.Html;
import be.vsol.tools.JavaScript;
import be.vsol.util.Icon;
import be.vsol.util.Int;
import be.vsol.util.Str;
import be.vsol.vsol6.controller.api.API;
import be.vsol.vsol6.model.enums.Language;
import be.vsol.vsol6.services.Vsol4Service;
import be.vsol.vsol6.session.Session;

import java.util.Map;

public class ServerHandler implements RequestHandler {

    private final ApiHandler apiHandler;
    private final WebHandler webHandler;

    public ServerHandler(Map<String, String> variables, API api) {
        this.webHandler = new WebHandler(variables);
        this.apiHandler = new ApiHandler(api);
    }

    @Override public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();

        if (path.matches("/api/.*")) {
            return apiHandler.respond(request);
        } else {
            return webHandler.respond(request);
        }
    }



}
