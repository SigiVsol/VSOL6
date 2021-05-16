package be.vsol.http;

public interface RequestHandler {

    HttpResponse<?> respond(HttpRequest<?> request);

//    HttpResponse<?> respond(HttpRequest<>)

}
