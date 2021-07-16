package be.vsol.vsol6.controller;

import be.vsol.database.model.DbQuery;
import be.vsol.http.Curl;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.Bytes;
import be.vsol.util.Log;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.database.MetaDb;
import be.vsol.vsol6.model.database.OrganizationDb;
import be.vsol.vsol6.model.meta.Computer;
import be.vsol.vsol6.model.meta.Network;
import be.vsol.vsol6.model.meta.Organization;
import be.vsol.vsol6.model.meta.Role;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.model.organization.Patient;
import org.json.JSONArray;
import org.json.JSONObject;

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

            case "sync" -> {
                ctrl.getSync().sync();
            }
            case "addMeta" -> {
                // Save new record & get queries
//                Organization organization = new Organization("VSOL");
//                Vector<DbQuery> queries = ctrl.getDb().getMetaDb().getOrganizations().save(organization);
                User user = new User("user1", null, null, "user1@vsol.test");
                Vector<DbQuery> queries = ctrl.getDb().getMetaDb().getUsers().save(user);
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
            case "change" -> {
                OrganizationDb animalsolutions = ctrl.getDb().getOrganizationDb("animalsolutions");
                Client gandalf = animalsolutions.getClients().getById("Gandalf 1");
                gandalf.setLastName("Gandalf the grey");
                Vector<DbQuery> queries = ctrl.getDb().getOrganizationDb("animalsolutions").getClients().save(gandalf);

                queries.forEach(dbQuery -> animalsolutions.getQueries().save(dbQuery));
            }
        }
    }

    // Handling Methods

    private void mem() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(Bytes.getSizeString(runtime.totalMemory() - runtime.freeMemory()));
    }

}
