package be.vsol.vsol6.model.config;

import be.vsol.tools.json;
import be.vsol.util.*;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

public class Config {

    @json public app app = new app();
    @json public console console = new console();
    @json public gui gui = new gui();
    @json public server server = new server();
    @json public vsol4 vsol4 = new vsol4();
    @json public orthanc orthanc = new orthanc();
    @json public db db = new db();
    @json public bridge bridge = new bridge();

    /**
     * Constructor
     * @param jsonDefaults JSONObject which contains all the default values for the configurations
     * @param overrides Map with all values to be overridden
     */
    public Config(JSONObject jsonDefaults, Map<String, String> overrides) {
        Json.load(this, jsonDefaults);

        try {
            for (String key : overrides.keySet()) {
                String[] subs = key.split("\\.", -1);
                Field field = getClass().getField(subs[0]);
                Object object = null;

                for (int i = 1; i < subs.length; i++) {
                    object = field.get(this);
                    field = object.getClass().getField(subs[i]);
                }

                String value = overrides.get(key);

                switch (field.getType().getSimpleName()) {
                    case "boolean" -> field.set(object, Bool.parse(value, false));
                    case "int" -> field.set(object, Int.parse(value, 0));
                    case "String" -> field.set(object, value);
                    case "File" -> field.set(object, new File(value));
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.err("Invalid parameter: " + e.getMessage());
        }
    }

    // Static Classes

    public static class app {
        public enum DataStorage { vsol4 }
        public enum DicomStorage { orthanc }
        @json public File home;
        @json public boolean debug;
        @json public DataStorage dataStorage;
        @json public DicomStorage dicomStorage;
    }

    public static class console {
        @json public boolean active;
    }

    public static class gui {
        @json public boolean active;
        @json public int width, height, x, y;
        @json public boolean maximized, undecorated, leftHanded;
        @json public String userId, organizationId;
    }

    public static class server {
        @json public boolean active;
        @json public String name;
        @json public int port, rowLimit;
    }

    public static class vsol4 {
        @json public boolean active;
        @json public String host, username, password;
        @json public int port, timeout, lifespan;
    }

    public static class orthanc {
        @json public boolean active;
        @json public String host;
        @json public int port, timeout;
    }

    public static class db {
        public enum Type { sqlite, mysql, postgresql }
        @json public Type type;
        @json public boolean active;
        @json public String host, user, password;
        @json public int port;
    }

    public static class bridge {
        @json public boolean active;
        @json public String host;
        @json public int port, timeout;
    }







}
