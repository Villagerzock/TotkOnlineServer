package net.villagerzock.event;

import net.villagerzock.Server;

public class ServerTickEvent extends Event {
    private final Server server;

    public ServerTickEvent(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
