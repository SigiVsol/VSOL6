package be.vsol.vsol6.controller.view;

import be.vsol.fx.FxConfig;
import be.vsol.fx.FxController;
import be.vsol.tools.Job;
import be.vsol.tools.Service;
import be.vsol.util.*;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.fx.App;
import be.vsol.vsol6.controller.fx.Splash;
import be.vsol.vsol6.controller.fx.app.Login;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Gui implements Service {

    private final FxConfig fxConfig;
    private final Stage primaryStage;

    private App app;
    private Login login;

    // Constructor

    public Gui(File home, Stage primaryStage, Map<String, String> namedParams) {
        this.primaryStage = primaryStage;
        fxConfig = new FxConfig(new File(home, "config/gui.json"), namedParams);
    }

    // Methods

    @Override public void start() {
        primaryStage.getIcons().add(Icon.getImage(true, "logo", 64));
        primaryStage.setTitle(Vsol6.getSig().getAppTitle());

        fxConfig.show(primaryStage);

        // show the splash screen until the initial screen is ready to be shown
        Splash splash = init("Splash");
        if (splash != null) splash.showInStage(primaryStage);

        new Job(() -> {
            app = init("App");
            login = init("app/Login");

            while (!Vsol6.isInitialized()) { Thr.sleep(100); } // wait for Vsol6 to initialize before showing the app screen

            assert app != null;
            app.showInStage(primaryStage);
        });
    }

    @Override public void stop() {
        fxConfig.save();
    }

    private <E extends FxController<?>> E init(String resource) {
        resource = Str.addon(resource, "fxml/", ".fxml");

        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(Resource.getInputStream(resource));
            E controller = fxmlLoader.getController();
            controller.setFxConfig(fxConfig);
            controller.init();
            return controller;
        } catch (IOException e) {
            Log.trace(e);
            return null;
        }
    }

    // Getters

    public Stage getPrimaryStage() { return primaryStage; }

    public App getApp() { return app; }

    public Login getLogin() { return login; }

}
