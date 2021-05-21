package be.vsol.vsol6.services;

import be.vsol.http.HttpServer;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.http.WebHandler;

public class WebServer extends HttpServer {

    public WebServer() {
        super(Vsol6.getSig().toString(), Vsol6.getSystemSession().getWebConfig().getPort(), new WebHandler());
    }

}
