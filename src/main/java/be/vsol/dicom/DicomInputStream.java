package be.vsol.dicom;

import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.VR;
import be.vsol.util.Bytes;
import be.vsol.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DicomInputStream extends ByteArrayInputStream {

    public DicomInputStream(byte[] buf) {
        super(buf);
    }

    public void readPreamble() {
        readNBytes(128); // preamble
        readNBytes(4); // obligatory "DCIM"
    }

    @Override public void close() {
        try {
            super.close();
        } catch (IOException e) {
            Log.trace(e);
        }
    }

    @Override public byte[] readNBytes(int len) {
        try {
            return super.readNBytes(len);
        } catch (IOException e) {
            Log.trace(e);
            return new byte[0];
        }
    }

    public boolean hasAttributes() {
        return available() >= 4;
    }

    public DicomAttribute readAttribute(boolean explicit) {
        if (available() < 4) return null;

        DicomAttribute result;
        DicomTag dicomTag = DicomTag.get(readNBytes(2), readNBytes(2));

        VR vr;
        if (explicit) {
            vr = VR.get(readNBytes(2));
            if (vr.getMetaLength() == 4) readNBytes(2);
        } else {
            vr = VR.Implicit;
        }

        int vl = Bytes.getInt(readNBytes(vr.getMetaLength())); // VL = value length

        if (vl == -1) { // undefined length: read until FFFE,E0DD
            byte[] value;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (true) {
                int a = read();
                if (a == 254) { // FE
                    int b = read();
                    if (b == 255) { // FF
                        int c = read();
                        if (c == 221) { // DD
                            int d = read();
                            if (d == 224) { // E0
                                break;
                            } else {
                                out.write(a);
                                out.write(b);
                                out.write(c);
                                out.write(d);
                            }
                        } else {
                            out.write(a);
                            out.write(b);
                            out.write(c);
                        }
                    } else {
                        out.write(a);
                        out.write(b);
                    }
                } else {
                    out.write(a);
                }
            }
            value = out.toByteArray();

            result = new DicomAttribute(dicomTag, value);
            result.setUndefinedLength(true);
        } else {
            byte[] value = readNBytes(vl);
            result = new DicomAttribute(dicomTag, value);
        }

        return result;
    }

}
