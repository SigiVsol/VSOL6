package be.vsol.vsol6.services;

import be.vsol.http.HttpServer;
import be.vsol.vsol6.controller.http.ServerHandler;
import be.vsol.vsol6.session.Session;

import java.util.Map;

public class ServerService extends HttpServer {

    public ServerService(Session session, Vsol4Service vsol4Service, Map<String, String> variables) {
        super(session.getConfig().server.name, session.getConfig().server.port, new ServerHandler(vsol4Service, variables));
    }

}
