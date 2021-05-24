package be.vsol.tools;

import be.vsol.util.Resource;

public class Html implements ContentType, ByteArray {

    private final byte[] bytes;

    public Html(byte[] bytes) {
        this.bytes = bytes;
    }

    public Html(String resource) {
        this.bytes = Resource.getBytes(resource);
    }

    @Override public byte[] getBytes() {
        return bytes;
    }

    @Override public String getContentType() {
        return "text/html";
    }
}
