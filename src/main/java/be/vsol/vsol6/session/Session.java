package be.vsol.vsol6.session;

import be.vsol.database.annotations.Db;
import be.vsol.database.structures.DbTable;
import be.vsol.util.*;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.*;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

public class Session {

    private LocalSystem system;
    private Organization organization;
    private User user;

    public Session(LocalSystem system, Organization organization, User user) {
        this.system = system;
        this.organization = organization;
        this.user = user;
    }

    private <E extends Config> E getConfig(Supplier<E> supplier) {
        E result = supplier.get();

        try {
            // read values from resource file
            JSONObject jsonObject = new JSONObject(new String(Resource.getBytes("config/" + result.getCategory() + ".json")));

            for (Field field : Reflect.getFields(result, Db.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();

                if (jsonObject.has(fieldName) && !jsonObject.isNull(fieldName)) {
                    switch (field.getType().getSimpleName()) {
                        case "boolean" -> field.set(result, jsonObject.getBoolean(fieldName));
                        case "int" -> field.set(result, jsonObject.getInt(fieldName));
                        case "long" -> field.set(result, jsonObject.getLong(fieldName));
                        case "double" -> field.set(result, jsonObject.getDouble(fieldName));
                        case "float" -> field.set(result, jsonObject.getFloat(fieldName));
                        case "String" -> field.set(result, jsonObject.getString(fieldName));
                    }
                }
            }

            // override from database
            DbTable<E> dbTable = Vsol6.getDatabaseManager().getDbSystem().getTable(result.getDbTableName());
            if (dbTable != null) {
                E current = dbTable.get("systemId = '" + system.getId() + "'", null);
                if (current != null) {
                    result.copy(current);
                }
            }

            // override with command line options
            Map<String, String> params = Vsol6.getParams().getNamed();

            for (Field field : Reflect.getFields(result, Db.class)) {
                field.setAccessible(true);
                String key = result.getCategory() + "." + field.getName();

                if (params.containsKey(key)) {
                    switch (field.getType().getSimpleName()) {
                        case "boolean" -> field.set(result, Bool.parse(params.get(key), false));
                        case "int" -> field.set(result, Int.parse(params.get(key), 0));
                        case "long" -> field.set(result, Lng.parse(params.get(key), 0L));
                        case "float" -> field.set(result, Flt.parse(params.get(key), 0f));
                        case "double" -> field.set(result, Dbl.parse(params.get(key), 0.0));
                        case "String" -> field.set(result, params.get(key));
                    }
                } else if (Vsol6.getParams().getUnnamed().contains(key)) {
                    System.out.println(key);
                    switch (field.getType().getSimpleName()) {
                        case "boolean", "Boolean" -> field.set(result, true);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }

        return result;
    }

    public void save(GuiConfig guiConfig) {
        DbTable<GuiConfig> guiConfigs = Vsol6.getDatabaseManager().getDbSystem().getGuiConfigs();
        GuiConfig current = guiConfigs.get("systemId = '" + system.getId() + "'", null);
        if (current == null) {
            current = new GuiConfig();
        }

        current.copy(guiConfig);
        current.setSystemId(system.getId());

        guiConfigs.save(current);
    }

    // Getters

    public LocalSystem getSystem() { return system; }

    public Organization getOrganization() { return organization; }

    public User getUser() { return user; }

    public Vsol4Config getVsol4Config() { return getConfig(Vsol4Config::new); }

    public OrthancConfig getOrthancConfig() { return getConfig(OrthancConfig::new); }

    public ServerConfig getServerConfig() { return getConfig(ServerConfig::new); }

    public GuiConfig getGuiConfig() { return getConfig(GuiConfig::new); }

    // Setters

    public void setSystem(LocalSystem system) { this.system = system; }

    public void setOrganization(Organization organization) { this.organization = organization; }

    public void setUser(User user) { this.user = user; }

}
