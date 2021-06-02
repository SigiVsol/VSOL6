package be.vsol.vsol6.view;

import be.vsol.tools.Job;
import be.vsol.tools.Sig;
import be.vsol.util.Icon;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Gui {

    private final Stage primaryStage;

    // Constructors

    public Gui(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Static Interface

    private static Stage splashStage;

    public static void showSplashScreen(Sig sig) {
        Platform.runLater(() -> {
            splashStage = new Stage(StageStyle.UNDECORATED);
            splashStage.setTitle(sig.toString());
            splashStage.getIcons().add(Icon.getImage(true, "logo", 128));

            FXMLLoader fxmlLoader = new FXMLLoader();
            try {
                Parent root = fxmlLoader.load(Resource.getInputStream("fxml/splash.fxml"));
                splashStage.setScene(new Scene(root));
                splashStage.show();
            } catch (IOException e) {
                Log.trace(e);
            }
        });
    }

    public static void hideSplashScreen() {
        if (splashStage != null) {
            Platform.runLater(() -> splashStage.hide());
        }
    }

}
