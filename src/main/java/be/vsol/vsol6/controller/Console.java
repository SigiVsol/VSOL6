package be.vsol.vsol6.controller;

import be.vsol.http.*;
import be.vsol.util.Bytes;
import be.vsol.util.Json;
import be.vsol.util.Log;
import be.vsol.vsol6.model.meta.Organization;
import be.vsol.vsol6.model.Query;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.Config;
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
                // Request (queries) & Response
                JSONObject syncRequest = new JSONObject();
                syncRequest.put("computerId", "comp1");
                syncRequest.put("organizationId", "VSOL");
                syncRequest.put("queries", new JSONArray(ctrl.getDb().getMetaDb().getQueries().getAll()));
                System.out.println("Request: " + syncRequest);
                HttpRequest httpRequest = new HttpRequest(HttpRequest.Method.POST, "/sync/meta", syncRequest);
                HttpResponse httpResponse = Curl.get(this.config.cloud.host, this.config.cloud.port, 1000, httpRequest);
                JSONObject response = httpResponse.getBodyAsJSONObject();
                System.out.println("Response: " + response);

                // Delete queries
                JSONArray queryIds = response.getJSONArray("queryIds");
                for (int i = 0; i < queryIds.length(); i++) {
                    String queryId = queryIds.getString(i);
                    Query query = ctrl.getDb().getMetaDb().getQueries().getById(queryId);
                    query.setDeleted(true);
                    ctrl.getDb().getMetaDb().getQueries().save(query);
                }

                // Update records
                JSONArray updates = response.getJSONArray("updates");
                System.out.println("'updates' : " + updates);
                for (int i = 0; i < updates.length(); i++) {
                    JSONObject update = updates.getJSONObject(i);
                    switch (update.getString("tableName")) {
                        case "organizations" -> {
                            JSONObject record =  update.getJSONObject("record");
                            Organization organization = Json.get(record, Organization::new);
                            ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                        }
                    }
                }

                // Send Ack
                JSONObject ack = new JSONObject();
                ack.put("computerId", "comp1");
                ack.put("organizationId", response.getString("organizationId"));
                ack.put("updateIds", response.getJSONArray("updateIds"));
                System.out.println("ack: " + ack);
                HttpRequest requestAck = new HttpRequest(HttpRequest.Method.POST, "/ack/meta", ack);
                Curl.get(this.config.cloud.host, this.config.cloud.port, 1000, requestAck);
            }
            case "add" -> {
                // Save new record & get queries
                Organization organization = new Organization("ORG_1");
                Vector<String> queries = ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                System.out.println("Queries: " + queries);

                // Save queries
                for (String sql : queries) {
                    Query query = new Query(sql.replace("'", "\""));
                    ctrl.getDb().getMetaDb().getQueries().save(query);
                }
            }
            case "change" -> {
                // Update record & get queries
                Organization organization = ctrl.getDb().getMetaDb().getOrganizations().getAll().firstElement();
                organization.setName(organization.getName().equals("ORG_1") ? "ORG_2" : "ORG_1");
                Vector<String> queries = ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                System.out.println("Queries: " + queries);

                // Save queries
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
