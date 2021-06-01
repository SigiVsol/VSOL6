package be.vsol.http;

import be.vsol.tools.Service;
import be.vsol.util.Log;
import be.vsol.tools.Job;
import be.vsol.util.Sema;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

public class HttpServer implements Service {

    private final String name;
    private final int port;
    private final RequestHandler requestHandler;
    private final Semaphore semaphore = new Semaphore(16);

    private ServerSocket serverSocket;

    public HttpServer(String name, int port, RequestHandler requestHandler) {
        this.name = name;
        this.port = port;
        this.requestHandler = requestHandler;
    }

    @Override public void start() {
        stop();
        new Thread(this).start();
    }

    @Override public void stop() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.trace(e);
            }
        }
    }

    @Override public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Log.out("Server " + name + " started. Listening on port " + port + ".");
            while (!serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    Sema.acquire(semaphore); {
                        new Job(() -> {
                            try {
                                HttpRequest httpRequest = new HttpRequest(socket.getInputStream());
                                if (httpRequest.isValid() && requestHandler != null) {
                                    HttpResponse httpResponse = requestHandler.respond(httpRequest);
                                    if (httpResponse != null) {
                                        httpResponse.getHeaders().put("Server", name);
                                        httpResponse.getHeaders().put("Date", DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(Instant.now()));
                                        httpResponse.getHeaders().put("Access-Control-Allow-Origin", "*");

                                        httpResponse.send(socket.getOutputStream());
                                    }
                                }
                            } catch (IOException e) {
                                Log.trace(e);
                            } finally {
                                try { socket.close(); } catch (IOException e) { Log.trace(e); }
                            }
                        });
                    } Sema.release(semaphore);
                } catch (SocketException e) {
                    if (!serverSocket.isClosed()) { // stop() closes the socket, so this is allowed
                        Log.trace(e);
                    }
                }
            }
        } catch (IOException e) {
            Log.trace(e);
        }
    }

    // Getters

    public RequestHandler getRequestHandler() { return requestHandler; }

    public String getName() { return name; }

    public boolean isRunning() {
        return serverSocket != null && !serverSocket.isClosed();
    }

}
