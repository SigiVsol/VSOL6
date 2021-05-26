package be.vsol.util;

import be.vsol.vsol6.Vsol6;

public class Var {

    public static String get(String key) {
        return switch (key) {
            case "app.version" -> Vsol6.getSig().getVersion();
            default -> key;
        };
    }

}
