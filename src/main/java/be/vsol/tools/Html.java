package be.vsol.tools;

import be.vsol.http.HttpRequest;
import be.vsol.http.HttpResponse;
import be.vsol.http.RequestHandler;
import be.vsol.util.*;
import be.vsol.vsol6.Vsol6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class Html implements ContentType, ByteArray {

    private final byte[] bytes;

    public Html(byte[] bytes) {
        this.bytes = bytes;
    }

    public Html(String resource, RequestHandler requestHandler, String language, Map<String, String> variables) {
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
                    if (requestHandler != null) {
                        for (String key : Key.get(line, '@')) { // @{...} : relay a new request to the webserver
                            HttpRequest httpRequest = new HttpRequest(key);
                            httpRequest.getHeaders().put("accept-language", language);
                            HttpResponse httpResponse = requestHandler.respond(httpRequest);
                            line = line.replace(Key.make(key, '@'), httpResponse.getBodyAsString());
                        }
                    }
                    if (language != null) {
                        for (String key : Key.get(line, '%')) { // %{...} ; translations
                            line = line.replace(Key.make(key, '%'), Lang.get(key, language));
                        }
                    }
                    if (variables != null) {
                        for (String key : Key.get(line, '$')) { // %{...} ; translations
                            line = line.replace(Key.make(key, '$'), variables.getOrDefault(key, key));
                        }
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
