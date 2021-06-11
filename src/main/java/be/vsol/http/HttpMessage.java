package be.vsol.http;

import be.vsol.dicom.Dicom;
import be.vsol.img.Jpg;
import be.vsol.img.Png;
import be.vsol.tools.*;
import be.vsol.util.Lang;
import be.vsol.util.Log;
import be.vsol.util.Int;
import be.vsol.vsol6.model.enums.Language;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public abstract class HttpMessage {

    public static final String defaultHttpVersion = "HTTP/1.1";

    protected String httpVersion;
    protected Map<String, String> headers;
    protected byte[] body;
    protected boolean valid = false;

    // Constructors

    protected HttpMessage() {
        httpVersion = defaultHttpVersion;
        headers = new HashMap<>();
    }

    protected HttpMessage(Object body) {
        this();

        if (body != null) {
            this.body = ByteArray.get(body);

            headers.put("Content-Type", ContentType.get(body));
            headers.put("Content-Length", "" + this.body.length);
        }
    }

    protected HttpMessage(InputStream inputStream) {
        this();

        HttpInputStream httpInputStream = new HttpInputStream(inputStream);

        String line = httpInputStream.readLine();
        parseFirstLine(line);

        while (!(line = httpInputStream.readLine()).isEmpty()) {
            String[] subs = line.split(": ", 2);
            headers.put(subs[0].toLowerCase().trim(), subs[1]);
        }

        String transferEncoding = headers.getOrDefault("transfer-encoding", null);
        String contentEncoding = headers.getOrDefault("content-encoding", null);

        if (transferEncoding == null) {
            int contentLength = Int.parse(headers.get("content-length"), 0);

            body = httpInputStream.readBytes(contentLength, contentEncoding);
        } else if (transferEncoding.equals("chunked")) {
            body = httpInputStream.readChunked(contentEncoding);
        }
    }

    // Abstract Methods

    public abstract String getFirstLine();

    public abstract void parseFirstLine(String line);

    // Methods

    public void send(OutputStream outputStream) {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));

        printWriter.println(getFirstLine());
        for (String key : headers.keySet()) {
            printWriter.println(key + ": " + headers.get(key));
        }
        printWriter.println(); // blank line between headers and content
        printWriter.flush(); // flush character output stream buffer

        if (body != null) {
            try {
                outputStream.write(body, 0, body.length);
                outputStream.flush();
            } catch (IOException e) {
                Log.trace(e);
            }
        }
    }

    @Override public String toString() {
        return getFirstLine();
    }

    // Getters

    public boolean isValid() { return valid; }

    public String getHttpVersion() { return httpVersion; }

    public Map<String, String> getHeaders() { return headers; }

    public byte[] getBody() { return body; }

    public Language getLanguage() {
        String[] acceptLanguages = headers.getOrDefault("accept-language", "").split(",", -1);
        for (String acceptLanguage : acceptLanguages) {
            if (acceptLanguage.matches(Language.getRegex())) {
                return Language.parse(acceptLanguage.substring(0, 2));
            }
        }
        return Language.getDefault();
    }

    public String getBodyAsString() { return body == null ? null : new String(body); }

    public JSONObject getBodyAsJSONObject() { return body == null ? null : new JSONObject(new String(body)); }

    public JSONArray getBodyAsJSONArray() { return body == null ? null : new JSONArray(new String(body)); }

    public Png getBodyAsPng() { return body == null ? null : new Png(body); }

    public Jpg getBodyAsJpg() { return body == null ? null : new Jpg(body); }

    public Dicom getBodyAsDicom() { return body == null ? null : new Dicom(body); }

    public Html getBodyAsHtml() { return body == null ? null : new Html(body); }

    public JavaScript getBodyAsJavaScript() { return body == null ? null : new JavaScript(body); }

    public Css getBodyAsCss() { return body == null ? null : new Css(body); }

}
