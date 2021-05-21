package be.vsol.vsol6.services;

import be.vsol.http.HttpServer;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.http.WebHandler;

public class ApiServer extends HttpServer {

    public ApiServer() {
        super(Vsol6.getSig().toString(), Vsol6.getSystemSession().getApiConfig().getPort(), new WebHandler());
    }

}
