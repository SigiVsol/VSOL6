package be.vsol.img;

import be.vsol.tools.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Img {

    protected byte[] bytes;

    public Img(byte[] bytes) {
        this.bytes = bytes;
    }

    public BufferedImage getBufferedImage() {
        if (bytes == null) return null;

        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            Log.trace(e);
            return null;
        }
    }

}
