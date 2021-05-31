package be.vsol.test.sigi;

import be.vsol.util.Resource;
import be.vsol.vsol4.*;
import be.vsol.vsol6.model.organization.Client;
import be.vsol.vsol6.services.Vsol4Service;
import be.vsol.vsol6.session.Session;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class TestVsol4 {
    public static void main(String[] args) {
        JSONObject jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));
        HashMap<String, String> params = new HashMap<>();
        Session session = new Session(jsonDefaults, params, null, null, null, null);
        Vsol4Service vsol4 = new Vsol4Service(session);
        vsol4.start();

        String username = "sigi";
//        Vsol4User user = vsol4.getUser("sigi");
        Vsol4Organization organization = vsol4.getDefaultOrganization(username);

        Vsol4Configuration configuration = vsol4.getConfiguration(organization.getId(), username);

        System.out.println(configuration.getMail_sender_name());



//
////        Vsol4Organization org = vsol4.getById(null, "ea9475c5-29f6-4da5-9d79-df3c552f519e", Vsol4Organization::new);
////        System.out.println(org);
////
////        Vsol4User test = vsol4.getById(null, "701edfed-3c40-4e1e-8122-c39b454e8f86", Vsol4User::new);
////        System.out.println(test);
////
////        Vsol4Client client = vsol4.getById(organization, "092e0421-7cef-4eb7-af5a-6f0b8b5196fd", Vsol4Client::new); // Febe
////
////        System.out.println(client.toString());
////        System.out.println(client.getContact().getAddress().getCity());
////
////        Vsol4Patient patient = vsol4.getById(organization, "9b2efd88-288d-452a-b87f-65ac751a20c1", Vsol4Patient::new); // Rex
////        System.out.println(patient);
////
////        System.out.println(patient.getBirthDate());
////
////        Vsol4Study study = vsol4.getById(organization, "d43df307-49a5-44e9-b085-ce117ebb62d1", Vsol4Study::new);
////        System.out.println(study.getDate());
//
//        Vector<Vsol4Client> vsol4Clients = vsol4.getAll(organization.getId(), "", Vsol4Client::new);
//
//        Vector<Client> clients = new Vector<>();
//        for (Vsol4Client vsol4Client : vsol4Clients) {
//            clients.add(new Client(vsol4Client));
//        }
//
//        Comparator<Client> comparator = Client.getComparator("", true);
//
//        clients.sort(comparator);
//
//        for (Client client : clients) {
//            System.out.println(client +  " " + client.getLastOpenedTime().truncatedTo(ChronoUnit.MILLIS));
//        }
//
////        System.out.println(clients);


    }

}
