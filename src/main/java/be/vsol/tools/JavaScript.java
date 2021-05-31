package be.vsol.tools;

import be.vsol.util.Key;
import be.vsol.util.Lang;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.vsol6.model.enums.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JavaScript implements ContentType, ByteArray {

    private final byte[] bytes;

    public JavaScript(byte[] bytes) {
        this.bytes = bytes;
    }

    public JavaScript(String resource) {
        bytes = Resource.getBytes(resource);
    }

    public JavaScript(String resource, Language language) {
        InputStream inputStream = Resource.getInputStream(resource);
        if (inputStream == null) {
            bytes = new byte[0];
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder result = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("//")) continue;
                    for (String key : Key.get(line, '%')) { // %{...} ; translations
                        line = line.replace(Key.make(key, '%'), Lang.get(key, language));
                    }

                    result.append(line).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                Log.trace(e);
            }

            bytes = result.toString().getBytes();
        }
    }

    @Override public byte[] getBytes() {
        return bytes;
    }

    @Override public String getContentType() {
        return "text/javascript";
    }
}
