package be.vsol.vsol6.controller.view;

import be.vsol.fx.EmptyController;
import be.vsol.fx.FxConfig;
import be.vsol.fx.FxController;
import be.vsol.tools.Job;
import be.vsol.tools.Service;
import be.vsol.util.*;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.fx.App;
import be.vsol.vsol6.controller.fx.Splash;
import be.vsol.vsol6.controller.fx.app.Login;
import be.vsol.vsol6.controller.fx.app.Explorer;
import be.vsol.vsol6.controller.fx.app.Settings;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Gui implements Service {

    private final FxConfig fxConfig;
    private final Stage primaryStage;

    private App app;
    private Login login;
    private Explorer explorer;
    private Settings settings;

    // Constructor

    public Gui(File home, Stage primaryStage, Map<String, String> namedParams) {
        this.primaryStage = primaryStage;
        fxConfig = new FxConfig(new File(home, "config/gui.json"), namedParams);
    }

    // Methods

    @Override public void start() {
        primaryStage.getIcons().add(Icon.getImage(true, "logo", 64));
        primaryStage.setTitle(Vsol6.getSig().getAppTitle());

        Splash splash = loadFxml("Splash");
        if (splash == null) return;
        splash.showInStage(primaryStage, false);
        fxConfig.show(primaryStage);

        new Job(() -> {
            app = loadFxml("App");
            login = loadFxml("app/Login");
            settings = loadFxml("app/Settings");

//            explorer = loadFxml("app/Explorer");
            explorer = new Explorer();

//            app.show(explorer);

            app.showInStage(primaryStage, true);
        });

    }

    @Override public void stop() {
        fxConfig.save();
    }

    private <N extends Node, C extends FxController<N>> C loadFxml(String resource) {
        resource = Str.addon(resource, "fxml/", ".fxml");

        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            N root = fxmlLoader.load(Resource.getInputStream(resource));
            C controller = fxmlLoader.getController();
            controller.setRoot(root);
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

    public Explorer getExplorer() { return explorer; }

    public Settings getSettings() { return settings; }

}
