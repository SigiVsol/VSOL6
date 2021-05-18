package be.vsol.test.sigi;

import be.vsol.util.Lang;

public class TestLang {
    public static void main(String[] args) {

        String key = "phrase";
        String lang = "nl";

        System.out.println(Lang.get(key, lang, 2, "Sigi", "Jan"));


    }



}
