package be.vsol.util;

import be.vsol.tools.json;
import be.vsol.vsol6.model.config.Config;
import be.vsol.vsol6.model.enums.Sex;
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
                } else if (field.getType().isEnum()) {
                    result.put(name, field.get(object));
                } else {
                    switch (field.getType().getSimpleName()) {
                        case "boolean" -> result.put(name, field.getBoolean(object));
                        case "int" -> result.put(name, field.getInt(object));
                        case "long" -> result.put(name, field.getLong(object));
                        case "double" -> result.put(name, field.getDouble(object));
                        case "float" -> result.put(name, field.getFloat(object));

                        case "Boolean", "Integer", "Long", "Float", "Double", "String" -> result.put(name, field.get(object));

                        case "LocalDate" -> result.put(name, Date.format(((LocalDate) field.get(object))));
                        case "LocalTime" -> result.put(name, Date.format(((LocalTime) field.get(object))));
                        case "LocalDateTime" -> result.put(name, Date.format(((LocalDateTime) field.get(object))));
                        case "Instant" -> result.put(name, Date.format(((Instant) field.get(object))));

                        default -> result.put(name, get(field.get(object)));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }

        return result;
    }

    public static <E> JSONArray getArray(Vector<E> vector) {
        JSONArray jsonArray = new JSONArray();

        for (E e : vector) {
            jsonArray.put(get(e));
        }

        return jsonArray;
    }

    public static <E> JSONArray getArray(E[] array) {
        JSONArray jsonArray = new JSONArray();
        for (E e : array) { jsonArray.put(e); }
        return jsonArray;
    }

    public static JSONArray getArray(boolean[] array) {
        JSONArray jsonArray = new JSONArray();
        for (boolean i : array) { jsonArray.put(i); }
        return jsonArray;
    }

    public static JSONArray getArray(int[] array) {
        JSONArray jsonArray = new JSONArray();
        for (int i : array) { jsonArray.put(i); }
        return jsonArray;
    }

    public static JSONArray getArray(long[] array) {
        JSONArray jsonArray = new JSONArray();
        for (long i : array) { jsonArray.put(i); }
        return jsonArray;
    }

    public static JSONArray getArray(float[] array) {
        JSONArray jsonArray = new JSONArray();
        for (float i : array) { jsonArray.put(i); }
        return jsonArray;
    }

    public static JSONArray getArray(double[] array) {
        JSONArray jsonArray = new JSONArray();
        for (double i : array) { jsonArray.put(i); }
        return jsonArray;
    }

    public static <E> void load(E object, JSONObject jsonObject) {
        try {
            for (Field field : Reflect.getFields(object, json.class)) {
                field.setAccessible(true);
                String name = field.getName();

                if (jsonObject.has(name)) {
                    if (jsonObject.isNull(name)) {
                        field.set(object, null);
                    } else if (field.getType().isEnum()) {
                        field.set(object, Enum.valueOf(field.getType().asSubclass(Enum.class), jsonObject.get(name).toString()));
                    } else {
                        switch (field.getType().getSimpleName()) {
                            case "boolean" -> field.set(object, jsonObject.getBoolean(name));
                            case "int" -> field.set(object, jsonObject.getInt(name));
                            case "long" -> field.set(object, jsonObject.getLong(name));
                            case "double" -> field.set(object, jsonObject.getDouble(name));
                            case "float" -> field.set(object, jsonObject.getFloat(name));

                            case "Boolean", "Integer", "Long", "Float", "Double", "String" -> field.set(object, jsonObject.get(name));

                            case "LocalDate" -> field.set(object, Date.parse(jsonObject.getString(name), (LocalDate) null));
                            case "LocalTime" -> field.set(object, Date.parse(jsonObject.getString(name), (LocalTime) null));
                            case "LocalDateTime" -> field.set(object, Date.parse(jsonObject.getString(name), (LocalDateTime) null));
                            case "Instant" -> field.set(object, Date.parse(jsonObject.getString(name), (Instant) null));

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

    public static Vector<Boolean> getBooleanVector(JSONArray jsonArray) {
        Vector<Boolean> result = new Vector<>();
        for (int i = 0; i < jsonArray.length(); i++) { result.add(jsonArray.getBoolean(i)); }
        return result;
    }

    public static Vector<Integer> getIntegerVector(JSONArray jsonArray) {
        Vector<Integer> result = new Vector<>();
        for (int i = 0; i < jsonArray.length(); i++) { result.add(jsonArray.getInt(i)); }
        return result;
    }

    public static Vector<Long> getLongVector(JSONArray jsonArray) {
        Vector<Long> result = new Vector<>();
        for (int i = 0; i < jsonArray.length(); i++) { result.add(jsonArray.getLong(i)); }
        return result;
    }

    public static Vector<Double> getDoubleVector(JSONArray jsonArray) {
        Vector<Double> result = new Vector<>();
        for (int i = 0; i < jsonArray.length(); i++) { result.add(jsonArray.getDouble(i)); }
        return result;
    }

    public static Vector<Float> getFloatVector(JSONArray jsonArray) {
        Vector<Float> result = new Vector<>();
        for (int i = 0; i < jsonArray.length(); i++) { result.add(jsonArray.getFloat(i)); }
        return result;
    }

    public static Vector<String> getStringVector(JSONArray jsonArray) {
        Vector<String> result = new Vector<>();
        for (int i = 0; i < jsonArray.length(); i++) { result.add(jsonArray.getString(i)); }
        return result;
    }

    public static <E> Vector<E> getVector(JSONArray jsonArray, Supplier<E> supplier) {
        Vector<E> result = new Vector<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            E e = supplier.get();
            load(e, jsonObject);
            result.add(e);
        }

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
