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

public class ServerHandler implements RequestHandler {

    @Override public HttpResponse respond(HttpRequest request) {
        System.out.println("< " + request);

        String path = request.getPath();

        if (path.equals("/") || path.matches(".*\\.html")) {
            return getHtml(request);
        } else if (path.matches(".*\\.js")) {
            return getJs(request);
        } else if (path.matches(".*\\.css")) {
            return getCss(request);
        } else if (path.matches("/ico/.*")) {
            return getIcon(request);
        } else if (path.matches("/lib/.*")) {
            return getLibrary(request);
        } else {
            return get404();
        }
    }

    private HttpResponse getHtml(HttpRequest request) {
        String path = Str.addonHead(request.getPath(), "html");
        if (path.equals("html/")) path += "app.html";
        return new HttpResponse(new Html(path, request.getLanguage()));
    }

    private HttpResponse getJs(HttpRequest request) {
        String path = Str.addonHead(request.getPath(), "js");
        return new HttpResponse(new JavaScript(path, request.getLanguage()));
    }

    private HttpResponse getCss(HttpRequest request) {
        String path = Str.addonHead(request.getPath(), "css");
        return new HttpResponse(new Css(path));
    }

    private HttpResponse getIcon(HttpRequest request) {
        boolean colored = request.getParameters().containsKey("colored");
        String name = request.getPath().split("/", -1)[2];
        int size = Int.parse(request.getParameters().get("size"), 16);

        Png icon = Icon.getPng(colored, name, size);
        return icon == null ? get404() : new HttpResponse(icon);
    }

    private HttpResponse getLibrary(HttpRequest request) {
        String lib = Str.cutoffHead(request.getPath(), "/lib/").toLowerCase();
        if (lib.equals("jquery")) {
            return new HttpResponse(new JavaScript("js/lib/jquery-3.6.0.min.js"));
        } else {
            return get404();
        }
    }

    private HttpResponse get404() {
        return new HttpResponse(HttpResponse.Status.NOT_FOUND, "404 :(");
    }

}
