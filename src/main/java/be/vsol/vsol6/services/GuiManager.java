package be.vsol.vsol6.services;

import be.vsol.fx.FxController;
import be.vsol.tools.Service;
import be.vsol.util.Icon;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.util.Str;
import be.vsol.vsol6.Vsol6;
import be.vsol.vsol6.controller.fx.App;
import be.vsol.vsol6.controller.fx.Splash;
import be.vsol.vsol6.controller.fx.app.Explorer;
import be.vsol.vsol6.controller.fx.app.Login;
import be.vsol.vsol6.controller.fx.app.Settings;
import be.vsol.vsol6.model.config.GuiConfig;
import be.vsol.vsol6.session.Session;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class GuiManager implements Service {

    private boolean running = false;
    private final Stage splashStage, primaryStage;

    private final Splash splash;
    private App app;
    private Login login;
    private Explorer explorer;
    private Settings settings;

    private final Session session;

    // Constructor

    public GuiManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.splashStage = new Stage(StageStyle.UNDECORATED);

        splash = loadFxml("Splash");

        // TODO: restore login for user, organization
        session = new Session(Vsol6.getSystem(), null, null);
    }

    // Methods

    @Override public void start() {
        app = loadFxml("App");
        login = loadFxml("app/Login");
        settings = loadFxml("app/Settings");
        explorer = new Explorer();

        running = true;
    }

    @Override public void stop() {
        running = false;

        session.save(new GuiConfig(primaryStage, session.getGuiConfig()));
    }

    public void showSplash() {
        setTitleAndLogo(splashStage);

        splashStage.setScene(new Scene(splash.getRoot()));
        splashStage.show();
    }

    public void showApp() {
        Platform.runLater(() -> {
            splashStage.hide();

            setTitleAndLogo(primaryStage);

            GuiConfig guiConfig = session.getGuiConfig();
            primaryStage.setWidth(guiConfig.getWidth());
            primaryStage.setHeight(guiConfig.getHeight());
            primaryStage.setX(guiConfig.getX());
            primaryStage.setY(guiConfig.getY());
            primaryStage.setMaximized(guiConfig.isMaximized());
            if (guiConfig.isUndecorated()) primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.setScene(new Scene(app.getRoot()));

//            explorer.loadUrl("https://www.google.com");
            app.show(explorer);

            primaryStage.show();
        });
    }

    private void setTitleAndLogo(Stage stage) {
        stage.setTitle(Vsol6.getSig().getAppTitle());
        stage.getIcons().add(Icon.getImage(true, "logo", 64));
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

    @Override public boolean isRunning() { return running; }

    public Session getSession() { return session; }

}
