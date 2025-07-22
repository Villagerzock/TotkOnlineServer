package net.villagerzock;

public abstract class Server {
    private static Server instance;

    public static Server getInstance() {
        return instance;
    }

    public static void setInstance(Server instance) {
        Server.instance = instance;
    }

    public abstract PluginLoader getPluginLoader();
}
