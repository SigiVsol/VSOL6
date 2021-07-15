package be.vsol.vsol6.controller.http;

import be.vsol.database.model.DbQuery;
import be.vsol.database.model.DbRecord;
import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.Json;
import be.vsol.vsol6.model.Update;
import be.vsol.vsol6.model.User;
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

import java.util.*;

public class CloudHandler implements RequestHandler {

    private final MetaDb metaDb;
    private final Vector<OrganizationDb> organizationDbs;

    public CloudHandler(MetaDb metaDb, Vector<OrganizationDb> organizationDbs) {
        this.metaDb = metaDb;
        this.organizationDbs = organizationDbs;
    }

    @Override public HttpResponse respond(HttpRequest request) {
        String path = request.getPath();
        System.out.println("New client with request: " + path);

        if (path.matches("/sync")) {
            System.out.println("sync queries");
            return syncQueries(request);
        } else if (path.matches("/ack")) {
            System.out.println("sync updates");
            return syncUpdates(request);
        }
        else if (path.matches("/sync/client/images")) {
            // TODO
            System.out.println("sync images");
            return HttpResponse.get404();
        }

        System.out.println("Unknown request");
        return HttpResponse.get404();
    }

    private HttpResponse syncQueries(HttpRequest request) {
        try {
            JSONObject json = request.getBodyAsJSONObject();
            System.out.println("REQUEST" + json);
            String computerId = json.getString("computerId");
            JSONArray queries = json.getJSONArray("queries");

            JSONObject jsonResponse = new JSONObject();

            Vector<Network> networks = metaDb.getNetworks().getAll("computerId='" + computerId + "'", null);
            boolean metaUpdatesAdded = false;
            for (Network network : networks) {
                OrganizationDb organizationDb = getOrganisationDb(network.getOrganizationId());
                JSONObject organization = new JSONObject();
                organization.put("id", network.getOrganizationId());
                if (network.isInitialized()) {
                    //Check meta updates for all organizations, so no need do it more then once
                    if(!metaUpdatesAdded) {
                        jsonResponse.put("queryIds", syncQueries(computerId,queries,metaDb));
                        addUpdatesToJson(computerId,jsonResponse, metaDb);
                        metaUpdatesAdded = true;
                    }

                    organization.put("queryIds", syncQueries(computerId, queries, organizationDb));
                    addUpdatesToJson(computerId, organization, organizationDb);
                }else{

                    if(!metaUpdatesAdded) {
                        jsonResponse.put("queryIds", new JSONArray());
                        jsonResponse.put("updateIds", new JSONArray());
                        jsonResponse.put("updates", new JSONArray());
                        metaUpdatesAdded = true;
                    }

                    getAllMetaInJson(computerId, network.getOrganizationId(), jsonResponse.getJSONArray("updates"));
                    getAllOrganizationInJson(organizationDb, organization);
                }
                jsonResponse.append("organizations", organization);
            }
            System.out.println("RESPONSE" + jsonResponse);
            return new HttpResponse(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.get400("Excepting a JSON with a client, organization and queries");
    }

    private Vector<String> syncQueries(String computerId, JSONArray jsonQueries, SyncDb syncDb) {
        Vector<String> queryIds = new Vector<>();
        Map<String, String> tableRecords = new HashMap<>();
        //save incoming UPDATE queries; if INSERT query, execute (once) instantly
        for (int i = 0; i < jsonQueries.length(); i++) {
            DbQuery query = Json.get(jsonQueries.getJSONObject(i), DbQuery::new);

            if (query.getType() == DbQuery.Type.UPDATE) {
                syncDb.getQueries().save(query);
            } else {
                syncDb.update(query.getQuery());
            }
            tableRecords.put(query.getRecordId(), query.getTableName());
            queryIds.add(query.getId());
        }

        //execute all (saved) queries involved and add updates
        tableRecords.forEach((recordId, tableName) -> {
            executeInvolvedQueries(syncDb, recordId);
            addUpdate(syncDb, computerId, tableName, recordId);
        });

        return queryIds;
    }

    private HttpResponse syncUpdates(HttpRequest request) {
        try{
            JSONObject json = request.getBodyAsJSONObject();
            System.out.println("REQUEST" + json);
            String computerId = json.getString("computerId");
            JSONArray metaUpdates = json.getJSONArray("updateIds");

            for(int i = 0; i < metaUpdates.length(); i++) {
                Update update = metaDb.getUpdates().get("id=" +  "'" + metaUpdates.getString(i) + "'");
                update.setDeleted(true);
                metaDb.getUpdates().save(update);
            }

            Vector<Network> networks = metaDb.getNetworks().getAll("computerId='" + computerId + "'", null);
            for(Network network: networks) {
                if (network.isInitialized()) {
                    OrganizationDb organizationDb = getOrganisationDb(network.getOrganizationId());
                    JSONArray orgUpdates = getFromOrganizationJson(json.getJSONArray("organizations"), network.getOrganizationId(), "updateIds");
                    for(int i = 0; i < orgUpdates.length(); i++) {
                        Update update = organizationDb.getUpdates().get("id=" +  "'" + orgUpdates.getString(i) + "'");
                        update.setDeleted(true);
                        organizationDb.getUpdates().save(update);
                    }
                }else{
                    network.setInitialized();
                    metaDb.getNetworks().save(network);
                }
            }
            System.out.println("RESPONSE:" + "OK");
            return new HttpResponse("OK");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.get400("Excepting a JSON with updateIds");
    }

    private void executeInvolvedQueries(SyncDb syncDb, String recordId) {
        Vector<DbQuery> queriesToExecute = syncDb.getQueries().getAll("recordId=" + "'" + recordId + "'", " createdTime ASC");
        for(DbQuery query : queriesToExecute) {
            syncDb.update(query.getQuery());
        }
    }

    private void addUpdate(SyncDb syncDb, String client, String tableName, String recordId) {
        Update update = new Update(client, tableName, recordId);
        syncDb.getUpdates().save(update);
    }

    private JSONObject addUpdatesToJson(String computerId, JSONObject jsonObject, SyncDb syncDb) {
        Vector<Update> dbUpdates = syncDb.getUpdates().getAll("computerId=" +  "'" + computerId + "'", null);
        Vector<String> updateIds = new Vector<>();
        Set<String> recordIds = new HashSet<>();
        JSONArray jsonUpdates = new JSONArray();
        for(Update update : dbUpdates) {
            String recordId = update.getRecordId();
            if(!recordIds.contains(recordId)) {
                JSONObject object;
                if(syncDb instanceof MetaDb) {
                    object = getMetaObjectByRecordId(update.getTableName(), recordId);
                }else{
                    object = getOrganizationObjectByRecordId(update.getTableName(), recordId, (OrganizationDb) syncDb);
                }
                jsonUpdates.put(object);
                recordIds.add(recordId);
            }
           updateIds.add(update.getId());
        }
        jsonObject.put("updateIds", updateIds);
        jsonObject.put("updates", jsonUpdates);
        return jsonObject;
    }

    private void getAllMetaInJson(String computerId, String organizationId, JSONArray updates) {

        Vector<Organization> organizations = metaDb.getOrganizations().getAll("id=" +  "'" + organizationId + "'",null);
        for(Organization organization: organizations) {
            updates.put(getObjectinJson("organizations", organization));
        }

        Vector<Role> roles = metaDb.getRoles().getAll("organizationId=" +  "'" + organizationId + "'",null);
        for(Role role: roles) {
            updates.put(getObjectinJson("roles", role));

            User user = metaDb.getUsers().getById(role.getUserId());
            updates.put(getObjectinJson("users", user));

            Vector<UserSetting> userSettings = metaDb.getUserSettings().getAll("userId=" +  "'" + role.getUserId() + "'",null);
            for(UserSetting userSetting: userSettings) {
                updates.put(getObjectinJson("userSettings", userSetting));
            }
        }
        //TODO: Do we need to send computer DBRecord?
        Computer computer = metaDb.getComputers().getById(computerId);
        updates.put(getObjectinJson("computers", computer));

        Vector<ComputerSetting> computerSettings = metaDb.getComputerSettings().getAll("computerId=" +  "'" + computerId + "'",null);
        for(ComputerSetting computerSetting: computerSettings) {
            updates.put(getObjectinJson("computerSettings", computerSetting));
        }

        //TODO: Networks doesn't have to be shared with the computers?
    }
    private JSONObject getAllOrganizationInJson(OrganizationDb organizationDb, JSONObject organization) {
        JSONArray orgObjects = new JSONArray();

        Vector<Client> clients = organizationDb.getClients().getAll();
        for(Client client: clients) {
            orgObjects.put(getObjectinJson("clients", client));
        }

        Vector<Patient> patients = organizationDb.getPatients().getAll();
        for(Patient patient: patients) {
            orgObjects.put(getObjectinJson("patients", patient));
        }

        Vector<Setting> settings = organizationDb.getSettings().getAll();
        for(Setting setting: settings) {
            orgObjects.put(getObjectinJson("settings", setting));
        }

        Vector<Study> studies = organizationDb.getStudies().getAll();
        for(Study study: studies) {
            orgObjects.put(getObjectinJson("studies", study));
        }

        organization.put("queryIds", new JSONArray());
        organization.put("updateIds", new JSONArray());
        organization.put("updates", orgObjects);
        return organization;
    }


    private JSONObject getObjectinJson(String tableName, DbRecord record) {
        JSONObject object = new JSONObject();
        object.put("tableName", tableName);
        object.put("record", Json.get(record));
        return object;
    }

    private JSONObject getMetaObjectByRecordId(String tableName, String recordId) {
        JSONObject object = new JSONObject();
        object.put("tableName", tableName);

        switch (tableName) {
            case "organizations" -> object.put("record", Json.get(metaDb.getOrganizations().getById(recordId)));
            case "computer" -> object.put("record", Json.get(metaDb.getComputers().getById(recordId)));
            case "computerSettings" -> object.put("record", Json.get(metaDb.getComputerSettings().getById(recordId)));
            case "roles" -> object.put("record", Json.get(metaDb.getRoles().getById(recordId)));
            case "users" -> object.put("record", Json.get(metaDb.getUsers().getById(recordId)));
            case "userSettings" -> object.put("record", Json.get(metaDb.getUserSettings().getById(recordId)));
            case "networks" -> object.put("record", Json.get(metaDb.getNetworks().getById(recordId)));
        }
        return object;
    }

    private JSONObject getOrganizationObjectByRecordId(String tableName, String recordId, OrganizationDb organizationDb) {
        JSONObject object = new JSONObject();
        object.put("tableName", tableName);

        switch (tableName) {
            case "clients" -> object.put("record", Json.get(organizationDb.getClients().getById(recordId)));
            case "patients" -> object.put("record", Json.get(organizationDb.getPatients().getById(recordId)));
            case "studies" -> object.put("record", Json.get(organizationDb.getStudies().getById(recordId)));
            case "setting" -> object.put("record", Json.get(organizationDb.getSettings().getById(recordId)));
        }
        return object;
    }

    private JSONArray getFromOrganizationJson(JSONArray organizations, String organizationId, String key) {
        for(int i = 0; i < organizations.length();i++) {
            JSONObject organization = organizations.getJSONObject(i);
            if(organization.getString("id").equals(organizationId)) {
                return organization.getJSONArray(key);
            }
        }
        return new JSONArray();
    }

    private OrganizationDb getOrganisationDb(String organizationId) {
        OrganizationDb selected = null;
        for(OrganizationDb organizationDb: organizationDbs) {
            if(organizationDb.getName().equals("db_" + organizationId.replace("-", "_"))){
                selected = organizationDb;
                break;
            }
        }
        return selected;
    }
}
