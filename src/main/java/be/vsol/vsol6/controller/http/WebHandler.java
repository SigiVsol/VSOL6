package be.vsol.vsol6.controller.http;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;

public class WebHandler implements RequestHandler {

    @Override public HttpResponse<?> respond(HttpRequest<?> request) {

        return switch (request.getPath()) {
            case "/index" -> getIndex();
            default -> new HttpResponse<>(HttpResponse.Status.INTERNAL_SERVER_ERROR, "Invalid request.");
        };


//        System.out.println(request.getBody());

//        System.out.println(request.getFirstLine());


    }

    private HttpResponse<String> getIndex() {
        return new HttpResponse<>("OK");
    }

}
