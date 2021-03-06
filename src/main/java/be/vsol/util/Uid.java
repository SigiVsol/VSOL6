package be.vsol.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Uid {

    public static String getRandom() {
        return UUID.randomUUID().toString();
    }

    public static String getHardwareCode() {
        String result = null;

        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                String command = "wmic csproduct get UUID";
                StringBuilder stringBuilder = new StringBuilder();

                Process process = Runtime.getRuntime().exec(command);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                result = stringBuilder.substring(stringBuilder.indexOf("\n"), stringBuilder.length()).trim().toLowerCase();
            }
        } catch (IOException e) {
            Log.trace(e);
        }

        return result;
    }

    public static String regex() {
        return "[0-9a-fA-F-]*";
    }

    public static String listRegex() {
        return "[0-9a-fA-F-]*(,[0-9a-fA-F-]*)*";
    }

}
