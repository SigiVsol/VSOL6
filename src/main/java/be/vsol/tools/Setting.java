package be.vsol.tools;

import be.vsol.database.structures.Database;
import be.vsol.util.Int;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Vector;

public abstract class Setting {

    public static void init(Map<String, String> namedParams, Vector<Database> databases, Class<?>... classes) {
        for (Class<?> _class : classes) {
            try {
                load(_class, namedParams, databases);
            } catch (IllegalAccessException e) {
                Log.trace(e);
            }
        }
    }

    private static void load(Class<?> _class, Map<String, String> namedParams, Vector<Database> databases) throws IllegalAccessException {
        String className = _class.getSimpleName();

        // 1. read the default values from the resource file
        JSONObject jsonObject = new JSONObject(new String(Resource.getBytes("settings/" + className + ".json")));
        for (Field field : _class.getFields()) {
            String fieldName = field.getName();

            if (jsonObject.isNull(fieldName)) {
                field.set(null, null);
            } else {
                switch (field.getType().getSimpleName()) {
                    case "int" -> field.set(null, jsonObject.getInt(fieldName));
                    case "String" -> field.set(null, jsonObject.getString(fieldName));
                }
            }
        }

        // 2. override with database settings, in order
        for (Database database : databases) {
//            DbTable<?> dbTable = database.getDbTable("settings_" + className);
//            if (dbTable != null) {
//
//            }
        }

        // 3. override with command line parameters
        if (namedParams != null) {
            for (Field field : _class.getFields()) {
                String key = className + "." + field.getName();
                if (namedParams.containsKey(key)) {
                    switch (field.getType().getSimpleName()) {
                        case "int" -> field.set(null, Int.parse(namedParams.get(key), 0));
                        case "String" -> field.set(null, namedParams.get(key));
                    }
                }
            }
        }

    }

}
