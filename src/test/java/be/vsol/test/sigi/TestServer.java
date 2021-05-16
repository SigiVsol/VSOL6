package be.vsol.test.sigi;

import be.vsol.http.*;

public class TestServer {

    public static void main(String[] args) {
        new HttpServer("Test Server", 8600, new Handler());
    }

    private static class Handler implements RequestHandler {
        @Override public HttpResponse<?> respond(HttpRequest<?> request) {


            for (String key : request.getHeaders().keySet()) {
                System.out.println(key + ": " + request.getHeaders().get(key));
            }


//            System.out.println("Handling...");
//            System.out.println(request);

            return new HttpResponse<>("Test 1 successful.");
        }
    }

}
