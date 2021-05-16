package be.vsol.http;

import be.vsol.tools.Log;

import java.io.IOException;
import java.net.Socket;

public class Curl {

    public static <E> HttpResponse<E> get(String host, int port, int timeout, HttpRequest<?> httpRequest) {
        try {
            Socket socket = new Socket(host, port);
            socket.setSoTimeout(timeout);

            httpRequest.getHeaders().put("Connection", "keep-alive");
            httpRequest.getHeaders().put("Accept", "*/*");
            httpRequest.getHeaders().put("Host", host + ":" + port);

            httpRequest.send(socket.getOutputStream());

            return new HttpResponse<>(socket.getInputStream());
        } catch (IOException e) {
            Log.trace(e);
            return null;
        }
    }

}
