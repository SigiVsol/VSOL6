package be.vsol.test.jurien;

import be.vsol.util.Resource;
import org.json.JSONObject;

import java.util.HashMap;

public class TestVsol4Configuration {
    public static void main(String[] args) {
        JSONObject jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));
        HashMap<String, String> params = new HashMap<>();
//        SessionOld sessionOld = new SessionOld(jsonDefaults, params, null, null, null, null);
//        Vsol4 vsol4 = new Vsol4(sessionOld);
//        vsol4.start();

//        String username = "firstUser";
//        Vsol4Organization organization = vsol4.getDefaultOrganization(username);
//
//        Vsol4Configuration configuration = vsol4.getConfiguration(organization.getId(), username);
//
//        System.out.println(configuration.getMail_sender_name());

    }
}
