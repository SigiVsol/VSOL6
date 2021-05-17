package be.vsol.fx.controls;

import java.io.File;

public class FileLabel extends XLabel<File> {

    public FileLabel() {
        super(null);
        setCaptioner(File::getAbsolutePath);
    }

}
