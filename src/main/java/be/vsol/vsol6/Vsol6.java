package be.vsol.vsol6;

import be.vsol.tools.Job;
import be.vsol.tools.LocalStorage;
import be.vsol.tools.Service;
import be.vsol.tools.Sig;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.vsol6.controller.api.API;
import be.vsol.vsol6.controller.api.Vsol4API;
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.Organization;
import be.vsol.vsol6.model.User;
import be.vsol.vsol6.services.*;
import be.vsol.vsol6.session.Session;
import javafx.application.Application;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Vector;

public class Vsol6 extends Application {

    private static final Sig sig = new Sig("VSOL6", 0, 0, 6, Sig.Publisher.SIGI_DEV, LocalDate.of(2021, Month.MAY, 15));

    private static final Vector<Service> services = new Vector<>();
    private static DatabaseService databaseService;
    private static GuiService guiService;
    private static Vsol4Service vsol4Service;
    private static API api;

    // Static Method

    public static void main(String[] args) {
        launch(args);
    }

    // Methods

    @Override public void start(Stage primaryStage) { try {
        File home = new File(getParameters().getNamed().getOrDefault("home", sig.getFolder()));
        LocalStorage localStorage = new LocalStorage(new File(home, "data/localStorage"));

        Log.init(new File(home, "logs"), sig.getAppTitle(), getParameters().getUnnamed().contains("debug"));
        Log.out("Starting " + sig + ".");
        Log.debug("Debug mode is on.");

        HashMap<String, String> variables = new HashMap<>(); {
            variables.put("app.version", sig.getVersion());
        }

        JSONObject jsonDefaults = new JSONObject(Resource.getString("config/defaults.json"));

        Session programSession = new Session(jsonDefaults, getParameters().getNamed(), null, null, null, null); // pre-database settings
        LocalSystem system = new LocalSystem();

        if (programSession.getConfig().gui.visible) {
            services.add(guiService = new GuiService(sig, home, primaryStage));
            guiService.showSplash(); // show splash screen while loading everything else in a thread
        }

        new Job(() -> {
            services.add(databaseService = new DatabaseService(home, programSession));

            Session systemSession = new Session(jsonDefaults, getParameters().getNamed(), databaseService, system, null, null);

            switch (systemSession.getConfig().app.backend) {
                case vsol4 -> {
                    services.add(vsol4Service = new Vsol4Service(systemSession));
                    api = new Vsol4API(vsol4Service);
                }
            }

            services.add(new OrthancService());
            services.add(new ConsoleService());

            services.add(new ServerService(systemSession, variables, api));

            for (Service service : services) {
                service.start();
            }

            if (guiService != null) {
                User user = databaseService.getUserDb().getUsers().getById(localStorage.get("user.id", null));
                Organization organization = databaseService.getOrganizationDb().getOrganizations().getById(localStorage.get("organization.id", null));

                guiService.showApp(systemSession, new Session(jsonDefaults, getParameters().getNamed(), databaseService, system, organization, user));
            }
        });
    } catch (Exception e) { Log.trace(e); System.exit(1); } }

    @Override public void stop() { try {
        for (Service service : services) {
            service.stop();
        }

        Log.out("Exiting.\n");
        System.exit(0);
    } catch (Exception e) { Log.trace(e); } }

}
