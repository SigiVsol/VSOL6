package be.vsol.test.sigi;

import be.vsol.http.*;
import org.json.JSONObject;

public class TestServer {

    public static void main(String[] args) {
        new HttpServer("Test Server", 8600, new Handler());
    }

    private static class Handler implements RequestHandler {
        @Override public HttpResponse respond(HttpRequest request) {

            System.out.println("REQUEST:");

            System.out.println(request.getPath());
            System.out.println(request.getParameters());
            System.out.println(request.getHeaders());
            System.out.println(request.getBodyAsJSONObject());

            return new HttpResponse("OK");
        }
    }

}
