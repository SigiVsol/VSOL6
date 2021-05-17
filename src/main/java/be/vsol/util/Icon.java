package be.vsol.util;

import be.vsol.img.Png;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Utility class to get icons from src/main/resources/[white|colored]/{name}.png in the desired size
 */
public class Icon {

    private static final HashMap<String, BufferedImage> bufferedImages = new HashMap<>();
    private static final HashMap<String, Png> pngs = new HashMap<>();
    private static final HashMap<String, byte[]> byteArrays = new HashMap<>();
    private static final HashMap<String, Image> images = new HashMap<>();

    public static BufferedImage getBufferedImage(boolean colored, String name, int size) {
        String code = getCode(colored, name, size);
        if (bufferedImages.containsKey(code)) {
            return bufferedImages.get(code);
        } else {
            name = Str.addon(name, "icons/" + (colored ? "colored" : "white") + "/", ".png");
            byte[] bytes = Resource.getBytes(name);
            if (bytes.length == 0) return null;
            Png png = new Png(bytes);
            BufferedImage result = png.getBufferedImage(Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, size, size);
            bufferedImages.put(code, result);
            return result;
        }
    }

    public static Png getPng(boolean colored, String name, int size) {
        String code = getCode(colored, name, size);
        if (pngs.containsKey(code)) {
            return pngs.get(code);
        } else {
            BufferedImage bufferedImage = getBufferedImage(colored, name, size);
            Png result = bufferedImage == null ? null : new Png(bufferedImage);
            pngs.put(code, result);
            return result;
        }
    }

    public static byte[] getBytes(boolean colored, String name, int size) {
        String code = getCode(colored, name, size);
        if (byteArrays.containsKey(code)) {
            return byteArrays.get(code);
        } else {
            Png png = getPng(colored, name, size);
            byte[] result = png == null ? new byte[0] : png.getBytes();
            byteArrays.put(code, result);
            return result;
        }
    }

    public static Image getImage(boolean colored, String name, int size) {
        String code = getCode(colored, name, size);
        if (images.containsKey(code)) {
            return images.get(code);
        } else {
            BufferedImage bufferedImage = getBufferedImage(colored, name, size);
            Image result = bufferedImage == null ? null : SwingFXUtils.toFXImage(bufferedImage, null);
            images.put(code, result);
            return result;
        }
    }

    private static String getCode(boolean colored, String name, int size) {
        return colored + "|" + name + "|" + size;
    }

}
