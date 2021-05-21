package be.vsol.vsol6.services;

import be.vsol.database.connection.DbDriver;
import be.vsol.database.structures.DbTable;
import be.vsol.tools.Service;
import be.vsol.util.Int;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.util.Uid;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.model.database.SystemDb;
import be.vsol.vsol6.model.setting.Setting;
import javafx.application.Application;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.time.Instant;

public class SettingsManager implements Service {

    private boolean running = false;
    private final Application.Parameters parameters;
    private final Class<?>[] settingsClasses;

    public SettingsManager(Application.Parameters parameters, Class<?>... settingsClasses) {
        this.parameters = parameters;
        this.settingsClasses = settingsClasses;
    }

    @Override public void start() {
        load(null, null, null);
        running = true;
    }

    @Override public void stop() {
        running = false;
    }

    public void load(LocalSystem localSystem, User user, Organization organization) { try {
        for (Class<?> settingsClass : settingsClasses) {
            String className = settingsClass.getSimpleName();

            // 1. read values from resource file
            JSONObject jsonObject = new JSONObject(new String(Resource.getBytes("settings/" + className + ".json")));
            for (Field field : settingsClass.getFields()) {
                String fieldName = field.getName();

                if (jsonObject.has(fieldName) && !jsonObject.isNull(fieldName)) {
                    switch (field.getType().getSimpleName()) {
                        case "int" -> field.set(null, jsonObject.getInt(fieldName));
                        case "String" -> field.set(null, jsonObject.getString(fieldName));
                    }
                }
            }

            // 2. override with organization-specific settings
            if (organization != null) {
                // TODO
            }

            // 3. override with user-specific settings
            if (user != null) {
                // TODO
            }

            // 4. override with system-specific settings
            if (localSystem != null) {

            }

            // 5. override with command line parameters
            for (Field field : settingsClass.getFields()) {
                String key = className + "." + field.getName();
                if (parameters.getNamed().containsKey(key)) {
                    switch (field.getType().getSimpleName()) {
                        case "int" -> field.set(null, Int.parse(parameters.getNamed().get(key), 0));
                        case "String" -> field.set(null, parameters.getNamed().get(key));
                    }
                } else if (parameters.getUnnamed().contains(key)) {
                    switch (field.getType().getSimpleName()) {
                        case "boolean", "Boolean" -> field.set(null, true);
                    }
                }
            }
        }
    } catch (IllegalAccessException e) { Log.trace(e); } }

    public <S extends Setting> void save(Class<S> settingsClass, String fieldName, Object value, LocalSystem localSystem) {
        String className = settingsClass.getSimpleName();
        SystemDb db = Vsol6.getDatabaseManager().getDbSystem();
        DbTable<S> settings = db.getTable("settings_" + className);

            Setting setting = settings.get(false, "system_id = '" + localSystem.getId() + "'", null);

        if (setting == null) {
            settings.getDb().update("INSERT INTO settings_" + className + " (id, createdTime, system_id, " + fieldName + ") VALUES ('" + Uid.getRandom() + "', '" + DbDriver.getString(Instant.now()) + "', '" + localSystem.getId() + "', '" + value + "')");
        } else {
            settings.getDb().update("UPDATE settings_" + className + " SET updatedTime = '" + DbDriver.getString(Instant.now()) + "', " + fieldName + " = '" + value + "' WHERE id = '" + setting.getId() + "'");
        }
    }

    public <E extends Setting> void save(Class<E> settingsClass, String fieldName, Object value, User user) {
        System.out.println(settingsClass.getSimpleName() + "." + fieldName + " -> " + value + " for " + user);
    }

    public <E extends Setting> void save(Class<E> settingsClass, String fieldName, Object value, Organization organization) {
        System.out.println(settingsClass.getSimpleName() + "." + fieldName + " -> " + value + " for " + organization);
    }

    // Getters

    @Override public boolean isRunning() {
        return running;
    }

}
