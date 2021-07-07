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
import be.vsol.vsol6.model.enums.Language;

import java.util.Map;

public class WebHandler implements RequestHandler {

    private final Map<String, String> variables;

    public WebHandler(Map<String, String> variables) {
        this.variables = variables;
    }

    @Override public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();
        Language language = request.getLanguage();
        Map<String, String> parameters = request.getParameters();

        if (path.matches("/(|explorer|viewer|settings)")) {
            return getApp(language);
        } else if (path.matches(".*\\.html")) {
            return getHtml(path, language);
        } else if (path.matches(".*\\.js")) {
            return getJs(path, language);
        } else if (path.matches(".*\\.css(\\.map)?")) {
            return getCss(path);
        } else if (path.matches("/icon/.*")) {
            return getIcon(path, parameters);
        } else if (path.matches("/lib/.*")) {
            return getLibrary(path);
        } else {
            return HttpResponse.get404();
        }
    }

    private HttpResponse getApp(Language language) {
        return new HttpResponse(new Html("html/app.html", this, language, variables));
    }

    private HttpResponse getHtml(String path, Language language) {
        path = Str.addonHead(path, "html");
        return new HttpResponse(new Html(path, this, language, variables));
    }

    private HttpResponse getJs(String path, Language language) {
        path = Str.addonHead(path, "js");
        return new HttpResponse(new JavaScript(path, language));
    }

    private HttpResponse getCss(String path) {
        path = Str.addonHead(path, "css");
        return new HttpResponse(new Css(path));
    }

    private HttpResponse getIcon(String path, Map<String, String> parameters) {
        String[] subs = path.split("/", 4); // /icon/add/16 -> ['', 'icon', 'add', '16']

        boolean colored = parameters.containsKey("colored");
        String name = subs[2];
        int size = Int.parse(subs[3], 16);

        Png icon = Icon.getPng(colored, name, size);
        return icon == null ? HttpResponse.get404() : new HttpResponse(icon);
    }

    private HttpResponse getLibrary(String path) {
        String lib = Str.cutoffHead(path, "/lib/").toLowerCase();
        if (lib.equals("jquery")) {
            return new HttpResponse(new JavaScript("js/lib/jquery-3.6.0.min.js"));
        } else {
            return HttpResponse.get404();
        }
    }



}
