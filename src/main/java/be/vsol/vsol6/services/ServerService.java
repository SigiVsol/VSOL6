//package be.vsol.vsol6.services;
//
//import be.vsol.http.HttpServer;
//import be.vsol.vsol6.controller.api.API;
//import be.vsol.vsol6.controller.http.ServerHandler;
//import be.vsol.vsol6.session.SessionOld;
//
//import java.util.Map;
//
//public class ServerService extends HttpServer {
//
//    public ServerService(SessionOld sessionOld, Map<String, String> variables, API api) {
//        super(sessionOld.getConfig().server.name, sessionOld.getConfig().server.port, new ServerHandler(sessionOld, variables, api));
//    }
//
//}
