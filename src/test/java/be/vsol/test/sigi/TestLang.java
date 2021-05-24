package be.vsol.test.sigi;

import be.vsol.util.Lang;

public class TestLang {
    public static void main(String[] args) {

        System.out.println(Lang.get("usage_example.|1|uno|dos", "nl"));
        System.out.println("1 " + Lang.get("client|1", "en") + ".");
        System.out.println("2 " + Lang.get("client|2", "en") + ".");

    }



}
