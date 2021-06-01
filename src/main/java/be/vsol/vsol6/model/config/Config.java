package be.vsol.vsol6.model.config;

import be.vsol.tools.json;

public class Config {

    @json public app app = new app();
    @json public gui gui = new gui();
    @json public server server = new server();
    @json public vsol4 vsol4 = new vsol4();
    @json public orthanc orthanc = new orthanc();
    @json public db db = new db();

    public static class app {
        public enum Backend { vsol4, bridge, vsol6 }
        @json public Backend backend;
    }

    public static class gui {
        @json public int width, height, x, y;
        @json public boolean visible, maximized, undecorated, leftHanded;
    }

    public static class server {
        @json public String name;
        @json public int port, rowLimit;
    }

    public static class vsol4 {
        @json public String host, username, password;
        @json public int port, timeout, lifespan;
    }

    public static class orthanc {
        @json public String host;
        @json public int port;
    }

    public static class db {
        @json public String type, host, user, password;
        @json public int port;
    }

}
