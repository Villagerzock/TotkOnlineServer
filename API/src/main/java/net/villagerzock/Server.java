package net.villagerzock;

import net.villagerzock.entities.EntityHandler;

import java.io.PrintStream;
import java.net.DatagramSocket;

public abstract class Server {
    private static Server instance;
    private final EntityHandler entityHandler;
    public static PrintStream originalPrintStream;

    protected Server(EntityHandler entityHandler) {
        this.entityHandler = entityHandler;
    }

    public static Server getInstance() {
        return instance;
    }

    public static void setInstance(Server instance) {
        Server.instance = instance;
    }

    public abstract PluginLoader getPluginLoader();
    public abstract void shutdown();
    public abstract DatagramSocket getServerSocket();
    public EntityHandler getEntityHandler(){
        return entityHandler;
    }
}
