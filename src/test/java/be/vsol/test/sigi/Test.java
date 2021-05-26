package be.vsol.test.sigi;

import be.vsol.util.Int;

public class Test {
    public static void main(String[] args) {
        String x = "136.0";

        System.out.println(x);
        System.out.println(Int.parse(x, 0));
    }
}
