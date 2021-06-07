package be.vsol.fx.util;

import be.vsol.util.Icon;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ImageIcon {

    private static final HashMap<String, Image> images = new HashMap<>();

    public static Image get(boolean colored, String name, int size) {
        String code = Icon.getCode(colored, name, size);
        if (images.containsKey(code)) {
            return images.get(code);
        } else {
            BufferedImage bufferedImage = Icon.getBufferedImage(colored, name, size);
            Image result = bufferedImage == null ? null : SwingFXUtils.toFXImage(bufferedImage, null);
            images.put(code, result);
            return result;
        }
    }

}
