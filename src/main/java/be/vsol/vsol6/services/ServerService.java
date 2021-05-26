package be.vsol.vsol6.services;

import be.vsol.http.HttpServer;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.http.ServerHandler;

public class ServerService extends HttpServer {

    public ServerService() {
        super(Vsol6.getLocalSession().getConfig().server.name, Vsol6.getLocalSession().getConfig().server.port, new ServerHandler());
    }

}
