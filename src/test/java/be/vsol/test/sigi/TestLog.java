package be.vsol.test.sigi;

import be.vsol.util.Log;


public class TestLog {
    public static void main(String[] args) {
        Log.out("This is an error.");

        String x = null;

        try {

        } catch (Exception e) {
            Log.trace(e);
        }


    }
}
