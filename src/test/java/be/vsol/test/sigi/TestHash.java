package be.vsol.test.sigi;

import be.vsol.util.Security;

public class TestHash {
    public static void main(String[] args) {
        String string = "f8461d1f-d60f-4d67-89ab-e9b3c44b9439";

        long hashCode = Math.abs(string.hashCode());

        System.out.println(hashCode);

        int code = Security.getCode(string);

        System.out.println(code);


    }
}
