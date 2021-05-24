package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.img.Png;
import be.vsol.tools.Html;
import be.vsol.util.Icon;
import be.vsol.util.Int;
import be.vsol.util.Str;

public class ServerHandler implements RequestHandler {

    public ServerHandler() {

    }

    @Override public HttpResponse respond(HttpRequest request) {
        System.out.println("< " + request);

        if (request.getPath().equals("/") || request.getPath().matches(".*\\.html")) {
            return getHtml(request);
        } else if (request.getPath().matches("/ico/.*")) {
            return getIcon(request);
        } else {
            return new HttpResponse(HttpResponse.Status.NOT_FOUND, "404 :(");
        }
    }

    private HttpResponse getHtml(HttpRequest request) {
        String path = "html" + request.getPath();
        if (path.equals("html/")) path += "app/index.html";

        Html html = new Html(path);
        return new HttpResponse(html);
    }

    private HttpResponse getIcon(HttpRequest request) {
        boolean colored = request.getParameters().containsKey("colored");
        String name = request.getPath().split("/", -1)[2];
        int size = Int.parse(request.getParameters().get("size"), 16);

        Png icon = Icon.getPng(colored, name, size);
        return icon == null ? new HttpResponse(HttpResponse.Status.NOT_FOUND, "Icon not found.") : new HttpResponse(icon);
    }

}
