package be.vsol.vsol6.session;

import be.vsol.database.structures.DbTable;
import be.vsol.util.*;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.config.Setting;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Session {

    private final LocalSystem system;
    private final Organization organization;
    private final User user;

    private Config config;

    public Session() {
        this(null, null, null);
    }

    public Session(LocalSystem system, Organization organization, User user) {
        this.system = system;
        this.organization = organization;
        this.user = user;

        loadConfig();
    }

    private void loadConfig() {
        // read defaults from resource file
        config = Json.get(Config::new, new JSONObject(Resource.getString("config/defaults.json")));

        HashMap<String, String> map = new HashMap<>();

        // override with system database settings
        if (Vsol6.getDb() != null && system != null) {
            Vector<Setting> settings = Vsol6.getDb().getSystem().getSettings().getAll("systemId = '" + system.getId() + "'");
            for (Setting setting : settings) {
                map.put(setting.getKey(), setting.getValue());
            }
        }

        // override with command line parameters
        Map<String, String> params = Vsol6.getParams();
        for (String key : params.keySet()) {
            map.put(key, params.get(key));
        }

        System.out.println("-> " + map);

        try {
            for (String key : map.keySet()) {
                String[] subs = key.split("\\.", -1);
                Field field = config.getClass().getField(subs[0]);
                Object object = null;

                for (int i = 1; i < subs.length; i++) {
                    object = field.get(config);
                    field = field.get(config).getClass().getField(subs[i]);
                }

                switch (field.getType().getSimpleName()) {
                    case "boolean" -> field.set(object, Bool.parse(map.get(key), false));
                    case "int" -> field.set(object, Int.parse(map.get(key), 0));
                    case "long" -> field.set(object, Lng.parse(map.get(key), 0L));
                    case "float" -> field.set(object, Flt.parse(map.get(key), 0f));
                    case "double" -> field.set(object, Dbl.parse(map.get(key), 0.0));
                    case "Boolean", "Integer", "Long", "Double", "Float", "String" -> field.set(object, map.get(key));
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.trace(e);
        }

    }

    public void saveSystem(Setting newSetting) {
        if (system != null) {
            String key = newSetting.getKey();
            String value = newSetting.getValue();

            DbTable<Setting> dbTable = Vsol6.getDb().getSystem().getSettings();
            Setting setting = dbTable.get("systemId = '" + system.getId() + "' AND key = '" + key + "'");
            if (setting == null) {
                setting = new Setting(key, value);
                setting.setSystemId(system.getId());
            } else {
                setting.setValue(value);
            }
            dbTable.save(setting);
        }
    }

    // Getters

    public LocalSystem getSystem() { return system; }

    public Organization getOrganization() { return organization; }

    public User getUser() { return user; }

    public Config getConfig() { return config; }

}
