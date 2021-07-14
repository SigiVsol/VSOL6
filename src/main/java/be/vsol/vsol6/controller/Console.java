package be.vsol.vsol6.controller;

import be.vsol.database.model.DbQuery;
import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.Bytes;
import be.vsol.util.Json;
import be.vsol.util.Log;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.meta.Computer;
import be.vsol.vsol6.model.meta.Network;
import be.vsol.vsol6.model.meta.Organization;
import be.vsol.vsol6.model.meta.Role;
import org.json.JSONArray;
import org.json.JSONObject;

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
            case "fillServerDb" -> {
                // META
                for (int i = 1; i <= 5; i++) {
                    Organization organization = new Organization("ORG" + i);
                    ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                    for (int j = 1; j <= 5; j++) {
                        String userName = "user" + i * 100 + j;
                        User user = new User(userName, null, null, userName + "@org" + i + ".test");
                        ctrl.getDb().getMetaDb().getUsers().save(user);
                        Role role = new Role(organization.getId(), user.getId(), j == 1 ? Role.Type.ADMIN : Role.Type.USER);
                        ctrl.getDb().getMetaDb().getRoles().save(role);
                    }
                    for (int k = 1; k <= 5; k++) {
                        String computerCode = String.valueOf(i * 1000 + k);
                        Computer computer = new Computer(computerCode, "alias" + computerCode);
                        ctrl.getDb().getMetaDb().getComputers().save(computer);
                        Network network = new Network(organization.getId(), computer.getId());
                        ctrl.getDb().getMetaDb().getNetworks().save(network);
                    }
                }
            }
            case "sync" -> {
                // Request (queries)
                JSONObject syncRequest = new JSONObject();
                syncRequest.put("computerId", "comp1");
                syncRequest.put("queries", new JSONArray(ctrl.getDb().getMetaDb().getQueries().getAll()));
                JSONArray syncOrgs = new JSONArray();
                {
                    JSONObject org = new JSONObject();
                    org.put("id", "VSOL");
                    org.put("queries", "");
                    syncOrgs.put(org);
                }
                syncRequest.put("orgs", syncOrgs);
                System.out.println("Request: " + syncRequest);
                HttpRequest httpRequest = new HttpRequest(HttpRequest.Method.POST, "/sync/meta", syncRequest);
                HttpResponse httpResponse = Curl.get(this.config.cloud.host, this.config.cloud.port, 1000, httpRequest);

                // Response
                JSONObject response = httpResponse.getBodyAsJSONObject();
                System.out.println("Response: " + response);
                JSONArray queryIds = response.getJSONArray("queryIds");
                JSONArray updates = response.getJSONArray("updates");
                JSONArray updateIds = response.getJSONArray("updateIds");
                JSONArray orgs = response.getJSONArray("orgs");

                // Delete queries
                for (int i = 0; i < queryIds.length(); i++) {
                    String queryId = queryIds.getString(i);
                    DbQuery query = ctrl.getDb().getMetaDb().getQueries().getById(queryId);
                    query.setDeleted(true);
                    ctrl.getDb().getMetaDb().getQueries().save(query);
                }

                // Update records
                for (int i = 0; i < updates.length(); i++) {
                    JSONObject update = updates.getJSONObject(i);
                    JSONObject record =  update.getJSONObject("record");
                    switch (update.getString("tableName")) {
                        case "organizations" -> {
                            Organization organization = Json.get(record, Organization::new);
                            ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                        }
                        case "users" -> {
                            User user = Json.get(record, User::new);
                            ctrl.getDb().getMetaDb().getUsers().save(user);
                        }
                    }
                }

                // Send Ack
                JSONObject ack = new JSONObject();
                ack.put("computerId", "comp1");
                ack.put("updateIds", updateIds);
                for (int i = 0; i < orgs.length(); i++) {
                    JSONObject orgAck = orgs.getJSONObject(i);
                    orgAck.remove("updates");
                }
                ack.put("orgs", orgs);
                System.out.println("ack: " + ack);
                HttpRequest requestAck = new HttpRequest(HttpRequest.Method.POST, "/ack/meta", ack);
                Curl.get(this.config.cloud.host, this.config.cloud.port, 1000, requestAck);
            }
            case "add" -> {
                // Save new record & get queries
                Organization organization = new Organization("ORG_1");
                Vector<DbQuery> queries = ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                User user = new User("user1", null, null, "user1@vsol.test");
                queries.addAll(ctrl.getDb().getMetaDb().getUsers().save(user));
                System.out.println("Queries: " + queries);

                // Save queries
                for (DbQuery query : queries) {
                    ctrl.getDb().getMetaDb().getQueries().save(query);
                }
            }
            case "change" -> {
                // Update record & get queries
                Organization organization = ctrl.getDb().getMetaDb().getOrganizations().getAll().firstElement();
                organization.setName(organization.getName().equals("ORG_1") ? "ORG_2" : "ORG_1");
                Vector<DbQuery> queries = ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                System.out.println("Queries: " + queries);

                // Save queries
                for (DbQuery query : queries) {
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
