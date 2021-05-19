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

public class HttpServer implements Runnable, Service {

    private boolean running = false;
    private final String name;
    private int port;
    private final RequestHandler requestHandler;
    private final Semaphore semaphore = new Semaphore(16);

    public HttpServer(String name, int port, RequestHandler requestHandler) {
        this.name = name;
        this.port = port;
        this.requestHandler = requestHandler;
    }

    @Override public void start() {
        new Thread(this).start();
    }

    @Override public void stop() {
        running = false;
    }

    @Override public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Log.out("Server " + name + " started. Listening on port " + port + ".");
            while (!serverSocket.isClosed()) {
                running = true;
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
                    Log.trace(e);
                }
                running = false;
            }
        } catch (IOException e) {
            Log.trace(e);
        }
    }

    // Getters

    @Override public boolean isRunning() {
        return running;
    }


    // Setters

    public void setPort(int port) { this.port = port; }

}
