package be.vsol.img;

import be.vsol.tools.ContentType;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.File;

public class Png extends Img implements ContentType {

    // Constructors

    public Png(byte[] bytes) {
        super(bytes);
    }

    public Png(File file) {
        super(file);
    }

    public Png(BufferedImage bufferedImage) {
        super(bufferedImage, "png");
    }

    public Png(BufferedImage bufferedImage, String formatName, Scalr.Method scalingMethod, Scalr.Mode scalingMode, int width, int height) {
        super(bufferedImage, formatName, scalingMethod, scalingMode, width, height);
    }

    // Getters

    @Override public String getContentType() {
        return "image/png";
    }

}
