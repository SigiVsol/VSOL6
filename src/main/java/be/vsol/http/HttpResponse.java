package be.vsol.http;

import be.vsol.util.Int;

import java.io.InputStream;

public class HttpResponse extends HttpMessage {

    public enum Status { OK, NOT_FOUND, INTERNAL_SERVER_ERROR }

    private int statusCode;
    private String reasonPhrase;

    // Constructors

    public HttpResponse(Object body) {
        this(Status.OK, body);
    }

    public HttpResponse(Status status, Object body) {
        super(body);

        statusCode = getStatusCode(status);
        reasonPhrase = getReasonPhrase(status);
    }

    public HttpResponse(InputStream inputStream) {
        super(inputStream);
    }

    // Methods

    @Override public String getFirstLine() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }

    @Override public void parseFirstLine(String topLine) {
        String[] subs = topLine.split(" ", 3);
        if (subs.length == 3) {
            httpVersion = subs[0];
            statusCode = Int.parse(subs[1], 500);
            reasonPhrase = subs[2];

            valid = httpVersion.startsWith("HTTP");
        }
    }

    private int getStatusCode(Status status) {
        return switch (status) {
            case OK -> 200;
            case NOT_FOUND -> 404;
            case INTERNAL_SERVER_ERROR -> 500;
        };
    }

    private String getReasonPhrase(Status status) {
        return switch (status) {
            case OK -> "OK";
            case NOT_FOUND -> "NOT FOUND";
            case INTERNAL_SERVER_ERROR -> "INTERNAL SERVER ERROR";
        };
    }

    // Getters

    public int getStatusCode() { return statusCode; }

    public String getReasonPhrase() { return reasonPhrase; }

}
