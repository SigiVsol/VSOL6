package be.vsol.http;

import be.vsol.util.Flow;
import be.vsol.util.Log;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class HttpInputStream extends BufferedInputStream {

    public HttpInputStream(InputStream in) {
        super(in);
    }

    public String readLine() {
        try {
            StringBuilder builder = new StringBuilder();

            while (true) {
                int b = read();

                if (b == -1) { // End of stream
//                    Log.err("HttpInputStream: Unexpected End of Stream");
                    break;
                } else if (b == 10) { // LF
                    break;
                } else if (b == 13) { // CR
                    Flow.doNothing();
                } else {
                    builder.append((char) b);
                }
            }
            return builder.toString();
        } catch (IOException e) {
            Log.trace(e);
            return null;
        }
    }

    public byte[] readChunked(String encoding) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while (true) {
            int chunkSize = Integer.parseInt(readLine(), 16);
            if (chunkSize == 0) {
                break;
            } else {
                byte[] chunk = readBytes(chunkSize, encoding);
                byteArrayOutputStream.writeBytes(chunk);
                readLine();
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    public byte[] readBytes(int length, String encoding) {
        try {
            return decompress(readNBytes(length), encoding);
        } catch (IOException e) {
            Log.trace(e);
        }

        return new byte[0];
    }

    private byte[] decompress(byte[] bytes, String encoding) {
        if (encoding != null) {
            if (encoding.equals("gzip")) {
                try {
                    return new GZIPInputStream(new ByteArrayInputStream(bytes)).readAllBytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

}
