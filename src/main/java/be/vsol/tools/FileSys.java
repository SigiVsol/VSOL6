package be.vsol.tools;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileSys {

    public static void create(File dir) {
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Log.out("Created '" + dir.getAbsolutePath() + "'.");
            }
        }
    }

    // Read

    public byte[] readBytes(File file) {
        return readBytes(file, new byte[0]);
    }

    public byte[] readBytes(File file, byte[] defaultValue) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException | NullPointerException e) {
            Log.err(e.getMessage());
            return defaultValue;
        }
    }

    public static String readString(File file, String defaultValue) {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException | NullPointerException e) {
            Log.err(e.getMessage());
            return defaultValue;
        }
    }

    // Write

    public static void writeString(File file, String string) {
        try {
            FileUtils.writeStringToFile(file, string, "utf8", false);
        } catch (IOException e) {
            Log.trace(e);
        }
    }

    public static void appendString(File file, String string, boolean ln) {
        if (ln) string += "\r\n";
        try {
            FileUtils.writeStringToFile(file, string, "utf8", true);
        } catch (IOException e) {
            Log.trace(e);
        }
    }

}
