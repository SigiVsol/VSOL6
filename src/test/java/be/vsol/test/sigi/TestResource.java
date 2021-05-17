package be.vsol.test.sigi;

import be.vsol.img.Png;
import be.vsol.util.Resource;
import be.vsol.util.Str;

public class TestResource {
    public static void main(String[] args) {

        String name = "add";
        boolean colored = true;

        String x = Str.addon(name, "icons/" + (colored ? "colored" : "white") + "/", ".png");

        System.out.println(x);


//        Png png = new Png(Resource.getBytes("icons/white/add.png"));

    }
}
