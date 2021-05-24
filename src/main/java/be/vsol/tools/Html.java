package be.vsol.tools;

import be.vsol.util.Key;
import be.vsol.util.Lang;
import be.vsol.util.Log;
import be.vsol.util.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Html implements ContentType, ByteArray {

    private final byte[] bytes;

    public Html(byte[] bytes) {
        this.bytes = bytes;
    }

    public Html(String resource) {
        this.bytes = Resource.getBytes(resource);
    }

    public Html(String resource, String language) {
        InputStream inputStream = Resource.getInputStream(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder result = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
//                for (String key : Key.get(line, '@')) { // @{...} : resources
//                    line = line.replace(Key.make(key, '@'), getStringResource(key, language));
//                }
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

    @Override public byte[] getBytes() {
        return bytes;
    }

    @Override public String getContentType() {
        return "text/html";
    }
}
