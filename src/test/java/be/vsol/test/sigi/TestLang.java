package be.vsol.test.sigi;

import be.vsol.util.Lang;
import be.vsol.vsol6.model.enums.Language;

public class TestLang {
    public static void main(String[] args) {

        System.out.println(Lang.get("usage_example.|1|uno|dos", Language.nl));
        System.out.println("1 " + Lang.get("client|1", Language.en) + ".");
        System.out.println("2 " + Lang.get("client|2", Language.en) + ".");

    }



}
