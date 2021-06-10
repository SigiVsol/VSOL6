package be.vsol.dicom;

import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.VR;
import be.vsol.tools.Hex;
import be.vsol.util.Bytes;
import be.vsol.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DicomInputStream extends ByteArrayInputStream implements Hex {

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
        if (dicomTag == null) return null;

        VR vr;
        if (!explicit || dicomTag.getVr() == null) {
            vr = null;
        } else {
            vr = VR.get(readNBytes(2));
            if (vr.isLongForm()) readNBytes(2);
        }

        int metaLength = (vr == null || vr.isLongForm()) ? 4 : 2; // how many bytes represent the length
        int vl = Bytes.getInt(readNBytes(metaLength)); // VL = value length

        if (vl < 0) { // undefined length: read until FFFE,E0DD
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            while (true) {
                int a = read();
                if (a == FE) {
                    int b = read();
                    if (b == FF) {
                        int c = read();
                        if (c == DD) {
                            int d = read();
                            if (d == E0) {
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

            result = new DicomAttribute(dicomTag, vr, out.toByteArray());
            result.setUndefinedLength(true);
        } else if (vl == 0) {
            result = new DicomAttribute(dicomTag, vr, new byte[0]);
        } else {
            result = new DicomAttribute(dicomTag, vr, readNBytes(vl));
        }

        return result;
    }

}
