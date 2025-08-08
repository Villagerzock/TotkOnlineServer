package net.villagerzock.entities;

import net.villagerzock.system.exceptions.EntityNotLoadedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface EntityHandler {
    Player getEntityHost(int i);
    Entity getEntity(int i);
    Collection<Player> getPlayers();
    void addPlayer(Player player);
    Player getPlayer(int hash);
    Player getPlayerByName(String name);
}
