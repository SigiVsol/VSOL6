package be.vsol.tools;

import be.vsol.util.Checksum;
import be.vsol.util.FileSys;
import be.vsol.util.Str;
import be.vsol.util.Uid;

import java.io.File;
import java.util.Base64;

public class LocalStorage {

    private final File dir;

    public LocalStorage(File dir) {
        this.dir = dir;
        FileSys.create(dir);
    }

    private String getMachineCode() {
        return Checksum.getSha256(Uid.getHardwareCode());
    }

    public String get(String key, String defaultValue) {
        File file = new File(dir, key);
        if (file.exists()) {
            byte[] bytes = FileSys.readBytes(file);
            return Str.cutoffHead(new String(Base64.getDecoder().decode(bytes)), getMachineCode());
        } else {
            return defaultValue;
        }
    }

    public void set(String key, String value) {
        File file = new File(dir, key);
        byte[] bytes = Base64.getEncoder().encode((getMachineCode() + value).getBytes());
        FileSys.writeBytes(file, bytes);
    }

}
