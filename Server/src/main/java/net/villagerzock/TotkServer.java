package net.villagerzock;

import net.villagerzock.event.Event;
import net.villagerzock.event.EventHandler;
import net.villagerzock.event.ServerTickEvent;
import org.reflections.Reflections;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class TotkServer extends Server {
    private static final TotkServer instance = new TotkServer();
    private final PluginLoader loader = new PluginLoader();
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final int TPS = 100;
    public static void main(String[] args) {
        instance.start(args);
    }
    private void start(String[] args){
        Server.setInstance(instance);
        System.out.println(GREEN + "  ______      __  __  ");
        System.out.println(" /_  __/___  / /_/ /__");
        System.out.println("  / / / __ \\/ __/ //_/");
        System.out.println(" / / / /_/ / /_/ ,<   ");
        System.out.println("/_/  \\____/\\__/_/|_|  " + RESET);
        System.out.println(RED + "   ____  _   ____    _____   ________\n" +
                "  / __ \\/ | / / /   /  _/ | / / ____/\n" +
                " / / / /  |/ / /    / //  |/ / __/   \n" +
                "/ /_/ / /|  / /____/ // /|  / /___   \n" +
                "\\____/_/ |_/_____/___/_/ |_/_____/   \n" + RESET);


        File pluginFolder = new File("plugins/");
        System.out.println("Plugin folder is at " +  pluginFolder.getAbsolutePath());
        if (!pluginFolder.exists()){
            pluginFolder.mkdirs();
            System.out.println("Created Plugin Folder!");
        }
        loader.loadFolder(pluginFolder);

        System.out.println("Found " + loader.getLoadedPlugins().length + " Plugins");

        System.out.println("Enabling Plugins");
        for (Plugin plugin : loader.getLoadedPlugins())
            plugin.onEnable();

        Timer timer = new Timer("Ticker");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        },0l,1000l / TPS);
    }
    public static final TotkServer getInstance(){
        return instance;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return loader;
    }

    private void tick(){
        if (Event.emit(new ServerTickEvent(this)))
            return;

    }

}