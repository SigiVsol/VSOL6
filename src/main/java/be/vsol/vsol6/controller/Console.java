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
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.enums.Language;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class Console implements Runnable {

    private final Ctrl ctrl;
    private final Config config;

    public Console(Ctrl ctrl, Config config) {
        this.ctrl = ctrl;
        this.config = config;
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
            case "sync" -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("client", "client_id");
                jsonObject.put("organization", "org_id");
                jsonObject.put("queries", new JSONArray(ctrl.getDb().getMetaDb().getQueries().getAll()));

                System.out.println("Request queries: " + jsonObject);
                HttpRequest httpRequest = new HttpRequest(HttpRequest.Method.POST, "/sync/client/data", jsonObject);

                HttpResponse httpResponse = Curl.get(this.config.cloud.host, this.config.cloud.port, 1000, httpRequest);
                System.out.println("Response: " + httpResponse.getBodyAsJSONObject());

                JSONArray updates = httpResponse.getBodyAsJSONObject().getJSONArray("updates");
                System.out.println("'updates' : " + updates);
                for (int i = 0; i < updates.length(); i++) {
                    JSONObject record = updates.getJSONObject(i);
                    switch (record.getString("type")) {
                        case "organization" -> {
                            Organization organization = Json.get(record.getJSONObject("object"), Organization::new);
                            ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                        }
                    }
                }

                JSONArray queryIds = httpResponse.getBodyAsJSONObject().getJSONArray("queries");
                for (int i = 0; i < queryIds.length(); i++) {
                    String queryId = queryIds.getString(i);
                    Query query = ctrl.getDb().getMetaDb().getQueries().getById(queryId);
                    query.setDeleted(true);
                    ctrl.getDb().getMetaDb().getQueries().save(query);
                }
            }
            case "add" -> {
                Organization organization = new Organization("ORG_1");
                Vector<String> queries = ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                System.out.println("Queries: " + queries);

                for (String sql : queries) {
                    Query query = new Query(sql.replace("'", "\""));
                    ctrl.getDb().getMetaDb().getQueries().save(query);
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
