package be.vsol.tools;

import be.vsol.util.Resource;

public class Css implements ContentType, ByteArray {

    private final byte[] bytes;

    public Css(byte[] bytes) {
        this.bytes = bytes;
    }

    public Css(String resource) {
        bytes = Resource.getBytes(resource);
    }

    @Override public byte[] getBytes() {
        return bytes;
    }

    @Override public String getContentType() {
        return "text/css";
    }
}
