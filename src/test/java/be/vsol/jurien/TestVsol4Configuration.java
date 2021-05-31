package be.vsol.jurien;

import be.vsol.util.Resource;
import be.vsol.vsol4.Vsol4Configuration;
import be.vsol.vsol4.Vsol4Organization;
import be.vsol.vsol6.services.Vsol4Service;
import be.vsol.vsol6.session.Session;
import org.json.JSONObject;

import java.util.HashMap;

public class TestVsol4Configuration {
    public static void main(String[] args) {
        JSONObject jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));
        HashMap<String, String> params = new HashMap<>();
        Session session = new Session(jsonDefaults, params, null, null, null, null);
        Vsol4Service vsol4 = new Vsol4Service(session);
        vsol4.start();

        String username = "firstUser";
        Vsol4Organization organization = vsol4.getDefaultOrganization(username);

        Vsol4Configuration configuration = vsol4.getConfiguration(organization.getId(), username);

        System.out.println(configuration.getMail_sender_name());

    }
}
