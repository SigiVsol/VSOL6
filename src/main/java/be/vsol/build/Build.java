package be.vsol.build;

import be.vsol.util.Bytes;
import be.vsol.util.Checksum;
import be.vsol.util.FileSys;
import be.vsol.vsol6.controller.Ctrl;

import java.io.File;

public class Build {
    public static void main(String[] args) {
        String[] builds = { "CL", "FX" };
        for (String build : builds) {
            System.out.println(Ctrl.sig + " (" + build + ")");
            File source = new File("out/" + Ctrl.sig.getAppTitle() + "_" + build + ".jar");
            File target = new File("out/" + Ctrl.sig.getAppTitle() + "_" + build + "_" + Ctrl.sig.getVersion() + ".jar");
            System.out.println(source.length() + " bytes (" + Bytes.getSizeString(source.length()) + ")");
            System.out.println(Checksum.getSha256(source));
            FileSys.copy(source, target);
            System.out.println();
        }

    }
}
