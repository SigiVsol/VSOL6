package be.vsol.test.sigi;

import be.vsol.orthanc.Orthanc;

public class TestOrthanc {

    public static void main(String[] args) {
        Orthanc orthanc = new Orthanc();
        orthanc.start("localhost", 8800, 2500);
    }

}
