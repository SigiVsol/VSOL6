package be.vsol.http;

import be.vsol.tools.Bytes;
import be.vsol.tools.Log;
import be.vsol.tools.type.Int;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpMessage<E> {

    public enum ContentType { TEXT, JSON, BYTES }
    public static final String defaultHttpVersion = "HTTP/1.1";

    protected String httpVersion = defaultHttpVersion;
    protected Map<String, String> headers = new HashMap<>();
    protected byte[] body;

    // Constructors

    protected HttpMessage() {
        this((E) null);
    }

    protected HttpMessage(E body) {
        this.headers = new HashMap<>();

        if (body != null) {
            if (body instanceof byte[]) {
                this.body = (byte[]) body;
                headers.put("Content-Type", getString(ContentType.BYTES));
            } else if (body instanceof String) {
                this.body = ((String) body).getBytes();
                headers.put("Content-Type", getString(ContentType.TEXT));
            } else if (body instanceof JSONObject) {
                this.body = body.toString().getBytes();
                headers.put("Content-Type", getString(ContentType.JSON));
            }

            if (this.body != null) {
                headers.put("Content-Length", "" + this.body.length);
            }
        }
    }

    protected HttpMessage(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = bufferedReader.readLine();
            parseFirstLine(line);

            while (!(line = bufferedReader.readLine()).isEmpty()) {
                String[] subs = line.split(": ", 2);
                headers.put(subs[0], subs[1]);
            }

            String buffer = "";
            if (headers.getOrDefault("Transfer-Encoding", "").equals("chunked")) {
                while (true) {
                    String remaining = bufferedReader.readLine();
                    if (remaining.equals("0")) break;
                    buffer += bufferedReader.readLine();
                }
            } else {
                int contentLength = Int.parse(headers.get("Content-Length"), 0);
                if (contentLength > 0) {
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer += line;
                    }
                }
            }
            body = buffer.getBytes();
        } catch (IOException e) {
            Log.trace(e);
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

    // Static Methods

    public static String getString(ContentType contentType) {
        return switch (contentType) {
            case TEXT -> "text/plain";
            case JSON -> "application/json";
            case BYTES -> "application/octet-stream";
        };
    }

    public static ContentType getContentType(String string) {
        if (string.startsWith("text/plain")) return ContentType.TEXT;
        else if (string.startsWith("application/json")) return ContentType.JSON;
        else if (string.startsWith("application/octet-stream")) return ContentType.BYTES;
        else return ContentType.BYTES; // TODO
    }

    // Getters

    public ContentType getContentType() { return getContentType(headers.get("Content-Type")); }

    public Map<String, String> getHeaders() { return headers; }

    @SuppressWarnings("unchecked")
    public E getBody() {
        return switch (getContentType(headers.get("Content-Type"))) {
            case BYTES -> (E) body;
            case TEXT -> (E) new String(body);
            case JSON -> (E) new JSONObject(new String(body));
        };
    }

}
