package net.villagerzock.entities;

import net.villagerzock.system.exceptions.EntityNotLoadedException;

import java.util.*;

public class EntityHandlerImpl implements EntityHandler {
    private final int entityAmount = 100;
    private List<Player>[] entityHost = new List[entityAmount];
    private Entity[] entities = new Entity[entityAmount];
    private final Map<Integer,Player> connectedPlayers = new HashMap<>();
    @Override
    public Player getEntityHost(int i){
        if (entityHost[i] == null)
            entityHost[i] = new ArrayList<>();
        if (entityHost[i].isEmpty())
            throw new EntityNotLoadedException(i);
        return entityHost[i].get(0);
    }
    @Override
    public Entity getEntity(int i){
        if (entities[i] == null)
            return new EntityImpl(10);
        return entities[i];
    }
    @Override
    public Collection<Player> getPlayers(){
        return connectedPlayers.values();
    }

    @Override
    public void addPlayer(Player player) {
        connectedPlayers.put(player.getIPHash(),player);
    }

    @Override
    public Player getPlayer(int hash) {
        return connectedPlayers.get(hash);
    }

    @Override
    public Player getPlayerByName(String name) {
        Player player = null;
        for (Player p : connectedPlayers.values()){
            if (p.getName() == name){
                player = p;
            }
        }
        return player;
    }
}
