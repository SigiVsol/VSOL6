package be.vsol.util;

import be.vsol.tools.json;
import be.vsol.vsol6.model.config.Config;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Vector;
import java.util.function.Supplier;

public class Json {

    public static JSONObject get(Object object) {
        JSONObject result = new JSONObject();

        try {
            for (Field field : Reflect.getFields(object, json.class)) {
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

                        case "Boolean", "Integer", "Long", "Float", "Double", "String" -> result.put(name, field.get(object));

                        case "LocalDate" -> result.put(name, field.get(object).toString());
                        case "LocalTime" -> result.put(name, ((LocalTime) field.get(object)).truncatedTo(ChronoUnit.SECONDS).toString());
                        case "LocalDateTime" -> result.put(name, ((LocalDateTime) field.get(object)).truncatedTo(ChronoUnit.SECONDS).toString());
                        case "Instant" -> result.put(name, ((Instant) field.get(object)).truncatedTo(ChronoUnit.MILLIS).toString());

                        default -> result.put(name, get(field.get(object)));
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
            for (Field field : Reflect.getFields(object, json.class)) {
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

                            case "Boolean", "Integer", "Long", "Float", "Double", "String" -> field.set(object, jsonObject.get(name));

                            case "LocalDate" -> field.set(object, LocalDate.parse(jsonObject.getString(name)));
                            case "LocalTime" -> field.set(object, LocalTime.parse(jsonObject.getString(name)));
                            case "LocalDateTime" -> field.set(object, LocalDateTime.parse(jsonObject.getString(name)));
                            case "Instant" -> field.set(object, Instant.parse(jsonObject.getString(name)));

                            default -> load(field.get(object), jsonObject.getJSONObject(name));
                        }
                    }
                }

            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }
    }

    public static <E> E get(JSONObject jsonObject, Supplier<E> supplier) {
        if (jsonObject == null) return null;

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

    public static Vector<JSONObject> iterate(JSONArray jsonArray) {
        Vector<JSONObject> result = new Vector<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(jsonArray.getJSONObject(i));
        }

        return result;
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
    public static JSONArray getOrDefault(JSONObject jsonObject, String key, JSONArray defaultValue) { try { return jsonObject.getJSONArray(key); } catch (JSONException e) { return defaultValue; } }

}
