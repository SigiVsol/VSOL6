package be.vsol.vsol6.controller.fx.app;

import be.vsol.vsol6.controller.fx.FxController;
import com.teamdev.jxbrowser.browser.Browser;
import com.teamdev.jxbrowser.engine.Engine;
import com.teamdev.jxbrowser.engine.EngineOptions;
import com.teamdev.jxbrowser.engine.ProprietaryFeature;
import com.teamdev.jxbrowser.engine.RenderingMode;
import com.teamdev.jxbrowser.view.javafx.BrowserView;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Explorer extends FxController<BrowserView> {

    private final Browser browser;

//    public Explorer(GuiService gui) {
//        this(gui, null);
//    }

    public Explorer(String url) {
//        this.gui = gui;

        System.setProperty("jxbrowser.license.key", "1BNDIEOFAYZEE9HQYY5M5ESGI6GCWTNOGK6CZWMWN94GQFFG96AB10J8YJC2KNKP5ZONEW"); // license
        disableWarning();

        Engine engine = Engine.newInstance(EngineOptions.newBuilder(RenderingMode.HARDWARE_ACCELERATED)
//                .userAgent(gui.getSig().toString())
//                .userDataDir(Path.of(new File(gui.getHome(), "app/browser").toURI()))
                .enableProprietaryFeature(ProprietaryFeature.AAC) // this is a licensed feature
                .enableProprietaryFeature(ProprietaryFeature.H_264) // this is a licensed feature
                .build());

        browser = engine.newBrowser();
//        setRoot(BrowserView.newInstance(browser));

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
