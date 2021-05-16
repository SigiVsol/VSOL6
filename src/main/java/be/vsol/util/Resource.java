package be.vsol.util;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class Resource {

    public static byte[] getBytes(String resource) {
        File file = new File("src/main/resources/" + resource);

        if (file.exists()) {
            return FileSys.readBytes(file);
        } else {
            try {
                return IOUtils.toByteArray(Resource.class.getClassLoader().getResourceAsStream(resource));
            } catch (IOException e) {
                Log.trace(e);
                return new byte[0];
            }
        }
    }

    public static InputStream getInputStream(String resource) {
        File file = new File("src/main/resources/", resource);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.trace(e);
                return new ByteArrayInputStream(new byte[0]);
            }
        } else {
            return Resource.class.getClassLoader().getResourceAsStream(resource);
        }
    }

}
