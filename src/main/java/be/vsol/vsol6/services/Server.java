package be.vsol.vsol6.services;

import be.vsol.http.HttpServer;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.http.ServerHandler;

public class Server extends HttpServer {

    public Server() {
        super(Vsol6.getSig().toString(), Vsol6.getSystemSession().getServerConfig().getPort(), new ServerHandler());
    }

}
