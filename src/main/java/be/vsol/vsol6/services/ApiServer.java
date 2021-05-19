package be.vsol.vsol6.services;

import be.vsol.http.HttpServer;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.http.WebHandler;
import be.vsol.vsol6.model.setting.api;

public class ApiServer extends HttpServer {

    public ApiServer() {
        super(Vsol6.getSig().toString(), api.port, new WebHandler());
    }

}
