package be.vsol.tools;

import org.json.JSONObject;

public interface ContentType {

    String getContentType();

    static String get(Object object) {
        if (object instanceof byte[]) {
            return "application/octet-stream";
        } else if (object instanceof ContentType) {
            return ((ContentType) object).getContentType();
        } else if (object instanceof JSONObject) {
            return "application/json";
        } else if (object instanceof String) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }

}
