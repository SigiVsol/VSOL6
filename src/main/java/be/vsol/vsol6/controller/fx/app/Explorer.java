package be.vsol.vsol6.controller.fx.app;

import be.vsol.vsol6.controller.Ctrl;
import be.vsol.vsol6.controller.fx.FxController;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;

public class Explorer extends FxController<BrowserView> {

    private final Browser browser;

    public Explorer(Ctrl ctrl, String url) {

        System.setProperty("jxbrowser.license.key", "1BNDIEOFAYZEE9HQYY5M5ESGI6GCWTNOGK6CZWMWN94GQFFG96AB10J8YJC2KNKP5ZONEW"); // license
        disableWarning();

        Engine engine = Engine.newInstance(EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
                .userAgent(ctrl.getSig().toString())
                .userDataDir(Path.of(new File(ctrl.getSystemSession().getConfig().app.home, "app/browser").toURI()))
                .enableProprietaryFeature(ProprietaryFeature.AAC) // this is a licensed feature
                .enableProprietaryFeature(ProprietaryFeature.H_264) // this is a licensed feature
                .build());

        browser = engine.newBrowser();
        super.load(ctrl, BrowserView.newInstance(browser));

        if (url != null) loadUrl(url);
    }

    public void loadUrl(String url) {
        browser.navigation().loadUrl(url);
    }

    private void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception ignored) { }
    }

}
