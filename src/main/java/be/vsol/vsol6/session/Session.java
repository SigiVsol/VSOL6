package be.vsol.vsol6.session;

import be.vsol.database.model.DbTable;
import be.vsol.vsol6.Ctrl;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.config.Setting;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.database.VsolDb;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Session {

    private final Ctrl ctrl;
    private final LocalSystem system;
    private final User user;
    private final Organization organization;

    private final Config config;

    // Constructors

    public Session(Ctrl ctrl, LocalSystem system) {
        this(ctrl, system, null, null);
    }

    public Session(Ctrl ctrl, LocalSystem system, User user, Organization organization) {
        this.ctrl = ctrl;
        this.system = system;
        this.user = user;
        this.organization = organization;

        Map<String, String> overrides = new HashMap<>();

        if (organization != null) {

        }
        if (user != null) {

        }
        if (system != null) {
            Vector<Setting> settings = ctrl.getDb().getSystemDb().getSettings().getAll("systemId = '" + system.getId() + "'");
            for (Setting setting : settings) {
                overrides.put(setting.getKey(), setting.getValue());
            }
        }

        this.config = new Config(ctrl.getJsonDefaults(), overrides);
    }

    // Methods

    public void saveSystem(Setting newSetting) {
        if (system != null) {
            String key = newSetting.getKey();
            String value = newSetting.getValue();

            DbTable<Setting> dbTable = ctrl.getDb().getSystemDb().getSettings();
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

    public User getUser() { return user; }

    public Organization getOrganization() { return organization; }

    public Config getConfig() { return config; }

}
