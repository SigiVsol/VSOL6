package be.vsol.tools;

import be.vsol.util.Log;
import be.vsol.util.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Csv {

    private final HashMap<String, HashMap<String, String>> map = new HashMap<>();

    // Constructors

    public Csv(String resource, char separator, boolean ignoreCase) {
        this(Resource.getInputStream(resource), separator, ignoreCase);
    }

    public Csv(InputStream inputStream, char separator, boolean ignoreCase) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String titleRow = bufferedReader.readLine();
            if (ignoreCase) titleRow = titleRow.toLowerCase();
            String[] fields = titleRow.split("" + separator, -1);

            if (fields.length > 1) {
                String row;
                while ((row = bufferedReader.readLine()) != null) {
                    if (ignoreCase) row = row.toLowerCase();
                    String[] values = row.split("" + separator, fields.length);
                    if (values.length > 1) {
                        String key = values[0];
                        HashMap<String, String> valueMap = new HashMap<>();
                        if (key != null && !key.isBlank()) {
                            for (int i = 1; i < fields.length && i < values.length; i++) {
                                valueMap.put(fields[i], values[i]);
                            }
                        }
                        map.put(key, valueMap);
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.trace(e);
        }
    }

    // Getters

    public HashMap<String, HashMap<String, String>> getMap() { return map; }

    public String getValue(String key, String valueKey) {
        HashMap<String, String> valueMap = map.get(key);
        if (valueMap == null) return null;
        else return valueMap.get(valueKey);
    }

}
