package be.vsol.vsol6.controller.fx.app;

import be.vsol.fx.FxController;
import be.vsol.util.Icon;
import be.vsol.vsol6.Vsol6;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Explorer extends FxController<BrowserView> {

    private final BrowserView browserView;

    public Explorer() {
        System.setProperty("jxbrowser.license.key", "1BNDIEOFAYZEE9HQYY5M5ESGI6GCWTNOGK6CZWMWN94GQFFG96AB10J8YJC2KNKP5ZONEW"); // license

        Engine engine = Engine.newInstance(EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                .userAgent(Vsol6.getSig().toString())
                .userDataDir(Path.of(Vsol6.getHome("app/browser").toURI()))
                .enableProprietaryFeature(ProprietaryFeature.AAC) // this is a licensed feature
                .enableProprietaryFeature(ProprietaryFeature.H_264) // this is a licensed feature
                .build());

        Browser browser = engine.newBrowser();

        browser.navigation().loadUrl("https://html5test.com");

        browserView = BrowserView.newInstance(browser);

        setRoot(browserView);
    }


    @Override public void init() {
//        webView.getEngine().load("https://www.google.com");
    }



}
