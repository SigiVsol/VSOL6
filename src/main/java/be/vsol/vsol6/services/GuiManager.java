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
import be.vsol.vsol6.model.LocalSystem;
import be.vsol.vsol6.model.setting.GuiConfig;
import be.vsol.vsol6.session.Session;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
        showSplash();

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

//        session.getGuiConfig().save(session.getSystem());

//        Vsol6.getSettingsManager().save(gui.class, "width", primaryStage.getWidth(), Vsol6.getSystem());

//        System.out.println(primaryStage.getWidth() + " x " + primaryStage.getHeight());
    }

    private void addListeners() {
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
//                width = newValue.intValue();
            }
        });
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
//                height = newValue.intValue();
            }
        });
        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
//                x = newValue.intValue();
            }
        });
        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
            if (!primaryStage.isMaximized() && !primaryStage.isIconified()) {
//                y = newValue.intValue();
            }
        });
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
//            maximized = newValue;
        });
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
            primaryStage.show();
        });
    }

    private void setTitleAndLogo(Stage stage) {
        stage.setTitle(Vsol6.getSig().getAppTitle());
        stage.getIcons().add(Icon.getImage(true, "logo", 64));
    }

    private <C extends FxController<?>> void show(C controller) {
        Platform.runLater(() -> {
            primaryStage.setScene(new Scene((Parent) controller.getRoot()));
            primaryStage.show();
        });
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
