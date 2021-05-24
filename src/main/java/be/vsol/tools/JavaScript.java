package be.vsol.tools;

import be.vsol.util.Resource;

public class JavaScript implements ContentType, ByteArray {

    private final byte[] bytes;

    public JavaScript(byte[] bytes) {
        this.bytes = bytes;
    }

    public JavaScript(String resource) {
        this.bytes = Resource.getBytes(resource);
    }

    @Override public byte[] getBytes() {
        return bytes;
    }

    @Override public String getContentType() {
        return "text/javascript";
    }
}
