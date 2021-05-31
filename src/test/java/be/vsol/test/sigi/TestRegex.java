package be.vsol.test.sigi;

import be.vsol.util.Uid;

public class TestRegex {

    public static void main(String[] args) {
        String uid = Uid.getRandom();

        System.out.println(uid);

        System.out.println(uid.matches("[0-9a-fA-F-]*"));
        System.out.println("sfrgezrsfgz".matches("[0-9a-fA-F-]*"));


    }

}
