package be.vsol.test.sigi;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import org.json.JSONObject;

import java.io.IOException;

public class TestCurl {
    public static void main(String[] args) throws IOException {
        testVsol4();
//        testOrthanc();
    }

    private static void testVsol4() {
        HttpRequest httpRequest = new HttpRequest("v0/organizations?User=firstuser");

        HttpResponse httpResponse = Curl.get("localhost", 8080, 1000, httpRequest);

        if (httpResponse != null && httpResponse.getStatusCode() == 200) {
            System.out.println(httpResponse.getStatusCode());
            System.out.println(httpResponse.getReasonPhrase());
            System.out.println(httpResponse.getHeaders());
            System.out.println(httpResponse.getBodyAsJSONObject().toString(1));
        }
    }

    private static void testOrthanc() {
        HttpRequest httpRequest = new HttpRequest("instances");

        HttpResponse httpResponse = Curl.get("localhost", 8800, 2500, httpRequest);

        if (httpResponse != null) {
            System.out.println(httpResponse.getStatusCode());
            System.out.println(httpResponse.getReasonPhrase());
            System.out.println(httpResponse.getHeaders());
            System.out.println(httpResponse.getBodyAsString());
//            System.out.println(httpResponse.getBody(new JSONObject()).toString(1));
        }

    }
}
