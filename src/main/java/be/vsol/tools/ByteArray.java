package be.vsol.tools;

public interface ByteArray {

    byte[] getBytes();

    static byte[] get(Object object) {
        if (object instanceof byte[]) {
            return (byte[]) object;
        } else if (object instanceof ByteArray) {
            return ((ByteArray) object).getBytes();
        } else {
            return object.toString().getBytes();
        }
    }

}
