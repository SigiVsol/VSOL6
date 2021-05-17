package be.vsol.util;

import be.vsol.annotations.StrMapField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StrMap {

    public static <E> void load(E object, Map<String, String> map, String prefix) {
        try {
            for (Field field : Reflect.getFields(object, StrMapField.class)) {
                field.setAccessible(true);
                String name = prefix + "." + field.getName();

                if (map.containsKey(name)) {
                    switch (field.getType().getSimpleName()) {
                        case "boolean" -> field.set(object, Bool.parse(map.get(name), false));
                        case "int" -> field.set(object, Int.parse(map.get(name), 0));
                        case "long" -> field.set(object, Lng.parse(map.get(name), 0L));
                        case "short" -> field.set(object, Shrt.parse(map.get(name), (short) 0));
                        case "double" -> field.set(object, Dbl.parse(map.get(name), 0.0));
                        case "float" -> field.set(object, Flt.parse(map.get(name), 0f));

                        case "String" -> field.set(object, map.get(name));
                    }
                }

            }
        } catch (IllegalAccessException e) {
            Log.trace(e);
        }
    }

    public static <E> E get(Supplier<E> supplier, Map<String, String> map, String prefix) {
        E result = supplier.get();
        load(result, map, prefix);
        return result;
    }

    public static Map<String, String> getByPairs(String... pairs) {
        HashMap<String, String> result = new HashMap<>();

        String key = null;
        for (String string : pairs) {
            if (key == null) key = string;
            else {
                result.put(key, string);
                key = null;
            }
        }

        return result;
    }

}
