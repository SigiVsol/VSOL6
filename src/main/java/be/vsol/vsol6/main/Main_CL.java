package be.vsol.vsol6.main;

import be.vsol.vsol6.controller.Ctrl;

import java.util.HashMap;

public class Main_CL {

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();

        for (String arg : args) {
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] subs = arg.split("=", 2);
                map.put(subs[0].substring(2), subs[1]);
            }
        }

        new Ctrl(map, null);
    }

}
