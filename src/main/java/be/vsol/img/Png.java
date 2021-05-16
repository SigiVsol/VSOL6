package be.vsol.img;

import be.vsol.tools.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class Png extends Img {

    public Png(byte[] bytes) {
        super(bytes);
    }

    public Png(File file) {

    }



}
