package be.vsol.dicom;

import be.vsol.dicom.model.DicomTag;
import be.vsol.dicom.model.DicomTag.Name;
import be.vsol.dicom.model.VR;
import be.vsol.tools.Hex;
import be.vsol.util.Bytes;
import be.vsol.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DicomOutputStream extends ByteArrayOutputStream implements Hex {

    private static final byte[] SEQ_END = { (byte) FE, (byte) FF, (byte) DD, (byte) E0 };

    public DicomOutputStream() { }

    public void writePreamble() {
        writeBytes(new byte[128]);
        writeBytes("DICM".getBytes());
    }

    @Override public void close() {
        try {
            super.close();
        } catch (IOException e) {
            Log.trace(e);
        }
    }

    public void writeAttribute(DicomAttribute attribute) {
        writeBytes(attribute.getDicomTag().getBytes());

        VR vr = attribute.getVr();

        if (vr == null) { // implicit
            writeBytes(Bytes.getByteArray(attribute.getValue().length, 4));
        } else { // explicit
            writeBytes(attribute.getVr().getTag().getBytes());
            if (vr.isLongForm()) writeBytes(new byte[2]);
            writeBytes(Bytes.getByteArray(attribute.getLength(), vr.isLongForm() ? 4 : 2));
        }

        writeBytes(attribute.getValue());

        if (attribute.getLength() == -1) {
            writeBytes(DicomTag.get(Name.SequenceDelimitationItem).getBytes());
            writeBytes(new byte[4]);
        }
    }

}
