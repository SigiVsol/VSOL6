package be.vsol.test.sigi;

import be.vsol.vsol6.services.SettingsManager;
import be.vsol.vsol6.session.Session;
import javafx.application.Application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSettings {

    public static void main(String[] args) {
        Application.Parameters parameters = new Application.Parameters() {
            @Override public List<String> getRaw() {
                return null;
            }

            @Override public List<String> getUnnamed() {
                return null;
            }

            @Override public Map<String, String> getNamed() {
                HashMap<String, String> result = new HashMap<>();
                result.put("vsol4.host", "test-vsol4-host-command-line");
                return result;
            }
        };

//        Session session = new Session(null, null, null);
//
//        session.getVsol4Config()
//
//
//
//
//        SettingsManager settingsManager = new SettingsManager(parameters);
//        settingsManager.start();
//
//        settingsManager.getVsol4(system, user, organization).getHost();




//        SettingsManager settingsManager = new SettingsManager(params, vsol4.class, orthanc.class);
//
//        settingsManager.load(null, null, null);
//
//        System.out.println(vsol4.host + ":" + vsol4.port);

//        settingsManager.save(vsol4.class, "host", "something-else", null, null, null);





//        Setting.init(params, new Vector<>(), vsol4.class, orthanc.class);
//
//        System.out.println(vsol4.host + ":" + vsol4.port);
//        System.out.println(orthanc.host + ":" + orthanc.port);
//
//
//        Setting.get(vsol4.class, vsol4.host, "");
//
//        Setting.set(vsol4.class, vsol4.host, "host", user);
//        Setting.set(vsol4.class, vsol4.host, "host", organization);
//        Setting.set(vsol4.class, vsol4.host, "host", system);
//
//        String host = Settings.vsol4.host;
//
//        Settings.init(params, user1, organization1);
//
//
//        Settings.vsol4.host.set("value", user1);
//        Settings.vsol4.host.set("value", organization1);
//        Settings.vsol4.host.set("value");



    }



}
