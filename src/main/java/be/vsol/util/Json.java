package be.vsol.util;

import be.vsol.tools.JsonField;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.function.Supplier;

public class Json {

    public static JSONObject get(Object object) {
        JSONObject result = new JSONObject();

        try {
            for (Field field : Reflect.getFields(object, JsonField.class)) {
                field.setAccessible(true);
                String name = field.getName();

                if (field.get(object) == null) {
                    result.put(name, JSONObject.NULL);
                } else {
                    switch (field.getType().getSimpleName()) {
                        case "boolean" -> result.put(name, field.getBoolean(object));
                        case "int" -> result.put(name, field.getInt(object));
                        case "long" -> result.put(name, field.getLong(object));
                        case "double" -> result.put(name, field.getDouble(object));
                        case "float" -> result.put(name, field.getFloat(object));

                        default -> result.put(name, field.get(object));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }

        return result;
    }

    public static <E> void load(E object, JSONObject jsonObject) {
        try {
            for (Field field : Reflect.getFields(object, JsonField.class)) {
                field.setAccessible(true);
                String name = field.getName();

                if (jsonObject.has(name)) {
                    if (jsonObject.isNull(name)) {
                        field.set(object, null);
                    } else {
                        switch (field.getType().getSimpleName()) {
                            case "boolean" -> field.set(object, jsonObject.getBoolean(name));
                            case "int" -> field.set(object, jsonObject.getInt(name));
                            case "long" -> field.set(object, jsonObject.getLong(name));
                            case "double" -> field.set(object, jsonObject.getDouble(name));
                            case "float" -> field.set(object, jsonObject.getFloat(name));

                            default -> field.set(object, jsonObject.get(name));
                        }
                    }
                }

            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }
    }

    public static <E> E get(Supplier<E> supplier, JSONObject jsonObject) {
        E result = supplier.get();
        load(result, jsonObject);
        return result;
    }

    public static void save(JSONObject jsonObject, File file) {
        FileSys.writeString(file, jsonObject.toString(1));
    }

    public static JSONObject load(File file) {
        return new JSONObject(FileSys.readString(file, "{}"));
    }

    public static String getOrDefault(JSONObject jsonObject, String key, String defaultValue) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static boolean getOrDefault(JSONObject jsonObject, String key, boolean defaultValue) { try { return jsonObject.getBoolean(key); } catch (JSONException e) { return defaultValue; } }
    public static int getOrDefault(JSONObject jsonObject, String key, int defaultValue) { try { return jsonObject.getInt(key); } catch (JSONException e) { return defaultValue; } }
    public static long getOrDefault(JSONObject jsonObject, String key, long defaultValue) { try { return jsonObject.getLong(key); } catch (JSONException e) { return defaultValue; } }
    public static double getOrDefault(JSONObject jsonObject, String key, double defaultValue) { try { return jsonObject.getDouble(key); } catch (JSONException e) { return defaultValue; } }
    public static float getOrDefault(JSONObject jsonObject, String key, float defaultValue) { try { return jsonObject.getFloat(key); } catch (JSONException e) { return defaultValue; } }

    public static JSONObject getOrDefault(JSONObject jsonObject, String key, JSONObject defaultValue) { try { return jsonObject.getJSONObject(key); } catch (JSONException e) { return defaultValue; } }

}
