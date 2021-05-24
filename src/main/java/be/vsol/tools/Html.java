package be.vsol.tools;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.util.Key;
import be.vsol.util.Lang;
import be.vsol.util.Log;
import be.vsol.util.Resource;
import be.vsol.vsol6.Vsol6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Html implements ContentType, ByteArray {

    private final byte[] bytes;

    public Html(byte[] bytes) {
        this.bytes = bytes;
    }

    public Html(String resource, String language) {
        InputStream inputStream = Resource.getInputStream(resource);
        if (inputStream == null) {
            bytes = new byte[0];
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder result = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("<!--") && line.trim().endsWith("-->")) continue;
                    for (String key : Key.get(line, '@')) { // @{...} : relay a new request to the webserver
                        HttpRequest httpRequest = new HttpRequest(key);
                        httpRequest.getHeaders().put("accept-language", language);
                        HttpResponse httpResponse = Vsol6.getHttpServer().getRequestHandler().respond(httpRequest);
                        line = line.replace(Key.make(key, '@'), httpResponse.getBody(""));
                    }
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

    @Override public String toString() {
        return new String(bytes);
    }

    @Override public byte[] getBytes() {
        return bytes;
    }

    @Override public String getContentType() {
        return "text/html";
    }
}
