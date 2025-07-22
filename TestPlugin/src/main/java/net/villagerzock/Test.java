package net.villagerzock;

import net.villagerzock.event.EventHandler;
import net.villagerzock.event.ServerTickEvent;

public class Test implements Plugin {

    @Override
    public void onEnable() {
        Events.Test();
    }

    @Override
    public void onLoad() {
        System.out.println("Loading Test");
        Events.Test();
    }
    @EventHandler
    public void onTick(ServerTickEvent e){
        System.out.println("TICK");
    }
}