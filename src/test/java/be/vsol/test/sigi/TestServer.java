package be.vsol.test.sigi;

import be.vsol.http.*;
import be.vsol.tools.Job;
import org.json.JSONObject;

public class TestServer {

    public static void main(String[] args) {
        HttpServer server = new HttpServer("Test Server", 8600, new Handler());
        server.start();


        new Job(2000, () -> {
            server.stop();
            System.out.println(server.isRunning() );
        });




    }

    private static class Handler implements RequestHandler {
        @Override public HttpResponse respond(HttpRequest request) {

            System.out.println("REQUEST:");

            System.out.println(request.getPath());
            System.out.println(request.getParameters());
            System.out.println(request.getHeaders());
//            System.out.println(request.getBodyAsJSONObject());

            return new HttpResponse("OK");
        }
    }

}
