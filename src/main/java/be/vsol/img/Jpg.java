package be.vsol.img;

import org.imgscalr.Scalr;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Jpg extends Img {

    // Constructors

    public Jpg(byte[] bytes) {
        super(bytes);
    }

    public Jpg(File file) {
        super(file);
    }

    public Jpg(BufferedImage bufferedImage) {
        super(bufferedImage, "jpg");
    }

    public Jpg(BufferedImage bufferedImage, String formatName, Scalr.Method scalingMethod, Scalr.Mode scalingMode, int width, int height) {
        super(bufferedImage, formatName, scalingMethod, scalingMode, width, height);
    }

    public Jpg(BufferedImage bufferedImage, float compressionQuality) {
        super();

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) writer.getDefaultWriteParam();
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(compressionQuality);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(out);
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(bufferedImage, null, null), jpegParams);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.bytes = out.toByteArray();
    }

}
