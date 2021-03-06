package be.vsol.http;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest extends HttpMessage {

    public enum Method { GET, POST, PUT, DELETE }

    private Method method;
    private String path;
    private Map<String, String> parameters;

    // Constructors

    public HttpRequest(String path) {
        this(Method.GET, path, null);
    }

    public HttpRequest(Method method, String path, Object body) {
        super(body);

        this.method = method;
        parsePath(path);
    }

    public HttpRequest(InputStream inputStream) {
        super(inputStream);
    }

    // Methods

    @Override public String getFirstLine() {
        String paramString = "";

        for (String key : parameters.keySet()) {
            paramString += (paramString.isEmpty() ? "" : "&") + key + "=" + parameters.get(key);
        }

        return method + " " + path + (paramString.isEmpty() ? "" : "?" + paramString) + " " + httpVersion;
    }

    @Override public void parseFirstLine(String line) {
        String[] subs = line.split(" ", 3);
        if (subs.length == 3) {
            method = HttpRequest.Method.valueOf(subs[0]);
            parsePath(subs[1]);
            httpVersion = subs[2];

            valid = httpVersion.startsWith("HTTP");
        }
    }

    private void parsePath(String path) {
        if (!path.startsWith("/")) path = "/" + path;

        parameters = new HashMap<>();

        if (path.contains("?")) {
            String parameterString = path.substring(path.indexOf('?') + 1);
            String[] params = parameterString.split("&", -1);
            for (String param : params) {
                String[] split = param.split("=", 2);
                String key = split[0];
                String value = split.length > 1 ? split[1] : "";
                value = URLDecoder.decode(value, Charset.defaultCharset());
                parameters.put(key, value);
            }

            path = path.substring(0, path.indexOf('?'));
        }

        this.path = path;
    }

    // Getters

    public String getPath() { return path; }

    public Method getMethod() { return method; }

    public Map<String, String> getParameters() { return parameters; }

}
