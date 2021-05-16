package be.vsol.img;

import java.io.File;

public class Raw extends Img {

    public Raw(byte[] bytes) {
        super(bytes);
    }

    public Raw(File file) {
        super(file);
    }

}
