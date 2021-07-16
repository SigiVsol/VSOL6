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
import be.vsol.vsol6.model.database.MetaDb;
import be.vsol.vsol6.model.database.OrganizationDb;
import be.vsol.vsol6.model.database.SyncDb;
import be.vsol.vsol6.model.meta.*;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import be.vsol.vsol6.model.organization.Setting;
import be.vsol.vsol6.model.organization.Study;
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
            case "metadb" -> {
                MetaDb metaDb = ctrl.getDb().getMetaDb();
                Organization org1 = new Organization("Veterinary Solutions");
                org1.setId("veterinarysolutions");
                Organization org2 = new Organization("Animal Solutions");
                org2.setId("animalsolutions");
                metaDb.getOrganizations().save(org1);
                metaDb.getOrganizations().save(org2);
                User user1 = new User("Sigi", "Sigi", "Janssens", "sigi@janssens.be");
                user1.setId("sigi");
                User user2 = new User("Juriën", "Juriën", "GeenIdee", "jurien@geenidee.be");
                user2.setId("jurien");
                User user3 = new User("Pieter", "Pieter", "Olaerts", "pieter@olaerts.be");
                user3.setId("pieter");
                User user4 = new User("Mathias", "Mathias", "MVG", "mathias@mbg.be");
                user4.setId("mathias");
                metaDb.getUsers().save(user1);
                metaDb.getUsers().save(user2);
                metaDb.getUsers().save(user3);
                metaDb.getUsers().save(user4);
                Role role1 = new Role(org1.getId(), user1.getId(), Role.Type.ADMIN);
                role1.setId("role1");
                Role role2 = new Role(org2.getId(), user1.getId(), Role.Type.USER);
                role2.setId("role2");
                Role role3 = new Role(org2.getId(), user2.getId(), Role.Type.ADMIN);
                role3.setId("role3");
                Role role4 = new Role(org1.getId(), user3.getId(), Role.Type.USER);
                role4.setId("role4");
                Role role5 = new Role(org1.getId(), user4.getId(), Role.Type.USER);
                role5.setId("role5");
                metaDb.getRoles().save(role1);
                metaDb.getRoles().save(role2);
                metaDb.getRoles().save(role3);
                metaDb.getRoles().save(role4);
                metaDb.getRoles().save(role5);
                Computer comp1 = new Computer("comp1", "comp1");
                comp1.setId("comp1");
                Computer comp2 = new Computer("comp2", "comp2");
                comp2.setId("comp2");
                Computer comp3 = new Computer("comp3", "comp3");
                comp3.setId("comp3");
                metaDb.getComputers().save(comp1);
                metaDb.getComputers().save(comp2);
                metaDb.getComputers().save(comp3);
                Network network1 = new Network(org1.getId(), comp1.getId());
                Network network2 = new Network(org2.getId(), comp2.getId());
                Network network3 = new Network(org1.getId(), comp3.getId());
                Network network4 = new Network(org2.getId(), comp3.getId());
                network1.setId("network1");
                network2.setId("network2");
                network3.setId("network3");
                network4.setId("network4");
                metaDb.getNetworks().save(network1);
                metaDb.getNetworks().save(network2);
                metaDb.getNetworks().save(network3);
                metaDb.getNetworks().save(network4);
            }

            case "organizations" -> {
                int i = 1;

                for(OrganizationDb organizationDb : ctrl.getDb().getOrganizationDbs()) {

                    Client client = new Client();
                    client.setLastName("Gandalf " + i);
                    client.setId("Gandalf " + i);
                    organizationDb.getClients().save(client);

                    for (int j = 1; j <= 2; j++) {
                        Patient patient = new Patient();
                        patient.setName("Shadowfax " +  j);
                        patient.setId("Shadowfax " +  j + "_" + client.getId());
                        organizationDb.getPatients().save(patient);
                    }
                    i++;
                }
            }


//            case "fillMetaDb" -> {
//                for (int i = 1; i <= 2; i++) {
//                    Organization organization = new Organization("org" + i);
//                    ctrl.getDb().getMetaDb().getOrganizations().save(organization);
//                    for (int j = 1; j <= 2; j++) {
//                        String userName = "user" + i * 100 + j;
//                        User user = new User(userName, null, null, userName + "@org" + i + ".test");
//                        ctrl.getDb().getMetaDb().getUsers().save(user);
//                        Role role = new Role(organization.getId(), user.getId(), j == 1 ? Role.Type.ADMIN : Role.Type.USER);
//                        ctrl.getDb().getMetaDb().getRoles().save(role);
//                    }
//                    for (int k = 1; k <= 2; k++) {
//                        String computerCode = "org" + i * 100 + k;
//                        Computer computer = new Computer(computerCode, "alias_" + computerCode);
//                        ctrl.getDb().getMetaDb().getComputers().save(computer);
//                        Network network = new Network(organization.getId(), computer.getId());
//                        ctrl.getDb().getMetaDb().getNetworks().save(network);
//                    }
//                }
//            }
//            case "fillOrgDb" -> {
//                ctrl.getDb().start();
//                for (int i = 1; i <= 2; i++) {
//                    Client client = new Client();
//                    client.setLastName("client" + i);
//                    ctrl.getDb().getOrganizationDbs().forEach(organizationDb -> organizationDb.getClients().save(client));
//                    for (int j = 1; j <= 2; j++) {
//                        Patient patient = new Patient();
//                        patient.setName("patient" + j + "_client" + i);
//                        ctrl.getDb().getOrganizationDbs().forEach(organizationDb -> organizationDb.getPatients().save(patient));
//                    }
//                }
//            }

            case "sync" -> {
                // Request (queries)
                JSONObject syncRequest = new JSONObject();
                syncRequest.put("computerId", "comp3");
                syncRequest.put("queries", new JSONArray(ctrl.getDb().getMetaDb().getQueries().getAll()));
                JSONArray syncOrganizations = new JSONArray();
                for (OrganizationDb organizationDb : ctrl.getDb().getOrganizationDbs()) {
                    JSONObject syncOrg = new JSONObject();
                    syncOrg.put("id", organizationDb.getName().substring(3).replace("_", "-"));
                    syncOrg.put("queries", organizationDb.getQueries().getAll());
                    syncOrganizations.put(syncOrg);
                }
                syncRequest.put("organizations", syncOrganizations);
                System.out.println("Request: " + syncRequest);
                HttpRequest httpRequest = new HttpRequest(HttpRequest.Method.POST, "/sync", syncRequest);
                HttpResponse httpResponse = Curl.get(this.config.cloud.host, this.config.cloud.port, 1000, httpRequest);

                // Response
                JSONObject response = httpResponse.getBodyAsJSONObject();
                System.out.println("Response: " + response);
                JSONArray queryIds = response.getJSONArray("queryIds");
                JSONArray updates = response.getJSONArray("updates");
                JSONArray updateIds = response.getJSONArray("updateIds");
                JSONArray organizations = response.getJSONArray("organizations");

                // Delete meta queries
                ctrl.getDb().getMetaDb().deleteQueries(queryIds);

                // Update meta records
                ctrl.getDb().getMetaDb().updateRecords(updates);

                // Org
                for (int i = 0; i < organizations.length(); i++) {
                    JSONObject org = organizations.getJSONObject(i);
                    String organizationId = org.getString("id");
                    JSONArray orgQueryIds = org.getJSONArray("queryIds");
                    JSONArray orgUpdates = org.getJSONArray("updates");
                    JSONArray orgUpdateIds = org.getJSONArray("updateIds");

                    OrganizationDb organizationDb = ctrl.getDb().getOrganizationDb(organizationId);
                    if (organizationDb == null) {
                        Organization organization = ctrl.getDb().getMetaDb().getOrganizations().getById(organizationId);
                        ctrl.getDb().addOrganizationDb(organization);
                        organizationDb = ctrl.getDb().getOrganizationDb(organizationId);
                    }

                    // Delete org queries
                    organizationDb.deleteQueries(orgQueryIds);

                    // Update org records
                    organizationDb.updateRecords(orgUpdates);
                }

                // Send Ack
                JSONObject ack = new JSONObject();
                ack.put("computerId", "comp3");
                ack.put("updateIds", updateIds);
                for (int i = 0; i < organizations.length(); i++) {
                    JSONObject orgAck = organizations.getJSONObject(i);
                    orgAck.remove("queryIds");
                    orgAck.remove("updates");
                }
                ack.put("organizations", organizations);
                System.out.println("ack: " + ack);
                HttpRequest requestAck = new HttpRequest(HttpRequest.Method.POST, "/ack", ack);
                Curl.get(this.config.cloud.host, this.config.cloud.port, 1000, requestAck);
            }
            case "addMeta" -> {
                // Save new record & get queries
//                Organization organization = new Organization("VSOL");
//                Vector<DbQuery> queries = (ctrl.getDb().getMetaDb().getOrganizations().save(organization));
                User user = new User("user1", null, null, "user1@vsol.test");
                Vector<DbQuery> queries = (ctrl.getDb().getMetaDb().getUsers().save(user));
                System.out.println("Queries: " + queries);

                // Save queries
                for (DbQuery query : queries) {
                    ctrl.getDb().getMetaDb().getQueries().save(query);
                }
            }
            case "addOrg" -> {
                // Save new record & get queries
                Client client = new Client();
                client.setLastName("client1");
                for (OrganizationDb organizationDb : ctrl.getDb().getOrganizationDbs()) {
                    Vector<DbQuery> queries = organizationDb.getClients().save(client);

                    // Save queries
                    for (DbQuery query : queries) {
                        organizationDb.getQueries().save(query);
                    }
                    System.out.println("Queries: " + queries);
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
