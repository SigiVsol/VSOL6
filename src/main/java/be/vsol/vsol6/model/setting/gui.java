package be.vsol.vsol6.model.setting;

import be.vsol.database.annotations.Db;

public class gui extends Setting {

    @Db public static int width, height;
    @Db public static Integer x, y;
    @Db public static boolean maximized, undecorated, leftHanded;

}
