package be.vsol.test.sigi;

import be.vsol.tools.Setting;
import be.vsol.vsol6.model.setting.orthanc;
import be.vsol.vsol6.model.setting.vsol4;

import java.util.HashMap;
import java.util.Vector;

public class TestSettings {

    public static void main(String[] args) {
        HashMap<String, String> params = new HashMap<>();

        Setting.init(params, new Vector<>(), vsol4.class, orthanc.class);

        System.out.println(vsol4.host + ":" + vsol4.port);
        System.out.println(orthanc.host + ":" + orthanc.port);
    }



}
