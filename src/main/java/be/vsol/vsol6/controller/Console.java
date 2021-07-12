package be.vsol.vsol6.controller;

import be.vsol.database.model.DbTable;
import be.vsol.http.*;
import be.vsol.util.Bytes;
import be.vsol.util.Json;
import be.vsol.util.Log;
import be.vsol.vsol6.controller.Ctrl;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.Query;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.enums.Language;
import org.json.JSONObject;

import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class Console implements Runnable {

    private final Ctrl ctrl;

    public Console(Ctrl ctrl) {
        this.ctrl = ctrl;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String command = scanner.nextLine();
                if (command == null) break;
                if (command.isBlank()) continue;

                String[] subs = command.split(" ", -1);
                if (subs.length > 0) {
                    handle(subs);
                }
            }
        } catch (IllegalStateException e) {
            Log.trace(e);
        }
    }

    private void handle(String[] subs) {
        switch (subs[0]) {
            case "exit" -> ctrl.exit();
            case "mem" -> mem();
            case "test" -> {
                Vector<Organization> organizations = ctrl.getDataStorage().getOrganizations("");
                Organization first = organizations.firstElement();

                Vector<User> users = ctrl.getDataStorage().getUsers(first.getId(), "");
                for (User user : users) {
                    System.out.println(user.getFirstName());
                }
            }
            case "server" -> {
                HttpServer httpServer = new HttpServer("TestServer");
                httpServer.start(8000, new RequestHandler() {
                    @Override
                    public HttpResponse respond(HttpRequest request) {
                        Map<String, String> parameters = request.getParameters();
                        System.out.println("New client with request:" + request.getPath());
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("message", "Hello, world!");
                        jsonResponse.put("user", "Pieter");
                        jsonResponse.put("organization", "Veterinary Solutions");
                        return new HttpResponse(jsonResponse);
                    }
                });
            }
            case "add" -> {
                Organization organization = new Organization("ORG_1");
                DbTable<Organization> dbTableOrganizations= ctrl.getDb().getMetaDb().getOrganizations();

                Vector<String> queries = dbTableOrganizations.save(organization);
                System.out.println("sql: " + queries);

                DbTable<Query> dbTableQuery = ctrl.getDb().getMetaDb().getQueries();

                for (String sql : queries){
                    Query query = new Query(sql.replace("'", "\""));
                    dbTableQuery.save(query);
                }
            }
        }
    }

    // Handling Methods

    private void mem() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(Bytes.getSizeString(runtime.totalMemory() - runtime.freeMemory()));
    }

}
