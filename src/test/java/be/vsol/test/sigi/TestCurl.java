package be.vsol.test.sigi;

import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import org.json.JSONObject;

import java.io.IOException;

public class TestCurl {
    public static void main(String[] args) throws IOException {

        HttpResponse<JSONObject> httpResponse = Curl.get("localhost", 8080, 1000, new HttpRequest<>("v0/organizations?User=firstuser"));

        if (httpResponse != null) {
            System.out.println(httpResponse.getBody().toString(1));
        }





//        HttpRequest<Void> httpRequest = new HttpRequest<>("/v0/organizations", StrMap.getByPairs("User", "firstuser"));
////        HttpRequest<Void> httpRequest = new HttpRequest<>("/instances", StrMap.getByPairs("User", "firstuser"));
////        System.out.println(httpRequest);
//
//        HttpResponse<JSONObject> httpResponse = Curl.get("localhost", 8080, 5000, httpRequest);
//
////        System.out.println(httpResponse);
//
//        assert httpResponse != null;
//        System.out.println( httpResponse.getBody().toString(1));




//
//
//
//        HashMap<String, String> parameters = new HashMap<>(); {
//            parameters.put("User", "firstuser");
//        }
////
//        HttpRequest<Object> httpRequest = new HttpRequest<>(HttpRequest.Method.GET, "v0/organizations", parameters, new HashMap<>());
//        System.out.println("REQ: " + httpRequest);
//
//        Socket socket = new Socket("localhost", 8080);
//        socket.setSoTimeout(5000);
//        httpRequest.send(socket.getOutputStream());
//
//        HttpResponse<JSONObject> httpResponse = new HttpResponse<>(socket.getInputStream());
//        System.out.println("RES: " + httpResponse);

    }
}
