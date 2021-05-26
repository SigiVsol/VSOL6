package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpRequest.Method;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.Lang;
import org.json.JSONObject;

public class API implements RequestHandler {
    @Override public HttpResponse respond(HttpRequest request) {
        System.out.println("<API " + request);

        String path = request.getPath();
        Method method = request.getMethod();

        if (method == Method.POST && path.matches("/api/authenticate")) {
            return authenticate(request);
        } else {
            return HttpResponse.get404();
        }
    }

    private HttpResponse authenticate(HttpRequest request) {
        JSONObject jsonObject = request.getBodyAsJSONObject();

        return HttpResponse.get404(Lang.get("Bad_credentials.", request.getLanguage()));
    }



}
