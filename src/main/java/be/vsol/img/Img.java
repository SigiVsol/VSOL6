package be.vsol.img;

import be.vsol.util.FileSys;
import be.vsol.util.Log;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public abstract class Img {

    protected byte[] bytes;

    // Constructors

    protected Img() { }

    protected Img(byte[] bytes) {
        this.bytes = bytes;
    }

    protected Img(File file) {
        this.bytes = FileSys.readBytes(file);
    }

    protected Img(BufferedImage bufferedImage, String formatName) {
        this(bufferedImage, formatName, null, null, 0, 0);
    }

    protected Img(BufferedImage bufferedImage, String formatName, Scalr.Method scalingMethod, Scalr.Mode scalingMode, int width, int height) {
        if (scalingMethod != null && scalingMode != null) {
            bufferedImage = Scalr.resize(bufferedImage, scalingMethod, scalingMode, width, height, (BufferedImageOp) null);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            boolean success = ImageIO.write(bufferedImage, formatName, out);
            if (!success) {
                Log.err("Can't convert BufferedImage to bytes.");
            }
        } catch (IOException e) {
            Log.trace(e);
        }
        bytes = out.toByteArray();
    }

    // Getters

    public byte[] getBytes() { return bytes; }

    public BufferedImage getBufferedImage() {
        if (bytes == null) return null;

        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            Log.trace(e);
            return null;
        }
    }

    public BufferedImage getBufferedImage(Scalr.Method scalingMethod, Scalr.Mode scalingMode, int width, int height) {
        BufferedImage result = getBufferedImage();
        return Scalr.resize(result, scalingMethod, scalingMode, width, height, (BufferedImageOp) null);
    }

}
