package net.villagerzock.commands;

import net.villagerzock.Server;
import net.villagerzock.entities.EntityHandler;
import net.villagerzock.entities.Player;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class KickCommand implements Command<KickCommand.KickOptions>{
    @Override
    public boolean onExecute(KickOptions options) {
        System.out.println("Kicked" + options.getPlayer().getName());
        return true;
    }

    @Override
    public KickOptions getOptions() {
        return new KickOptions();
    }

    public static class KickOptions{
        private Player player;
        @Option(name="-p",usage = "Sets the Player",required = true)
        public void setPlayer(String name){
            this.player = Server.getInstance().getEntityHandler().getPlayerByName(name);
        }

        public Player getPlayer() {
            return player;
        }
    }
}
