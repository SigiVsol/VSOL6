package be.vsol.util;

import be.vsol.img.Png;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

/**
 * Utility class to get icons from src/main/resources/[white|colored]/{name}.png in the desired size
 */
public class Icon {

    public static BufferedImage getBufferedImage(boolean colored, String name, int size) {
        name = Str.addon(name, "icons/" + (colored ? "colored" : "white") + "/", ".png");
        Png png = new Png(Resource.getBytes(name));
        return png.getBufferedImage(Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, size, size);
    }

    public static Png getPng(boolean colored, String name, int size) {
        BufferedImage bufferedImage = getBufferedImage(colored, name, size);
        return new Png(bufferedImage);
    }

    public static byte[] getBytes(boolean colored, String name, int size) {
        Png png = getPng(colored, name, size);
        return png.getBytes();
    }

    public static Image getImage(boolean colored, String name, int size) {
        BufferedImage bufferedImage = getBufferedImage(colored, name, size);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

}
