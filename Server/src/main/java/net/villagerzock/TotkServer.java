package net.villagerzock;

import imgui.ImGui;
import imgui.app.Application;
import net.villagerzock.commands.Command;
import net.villagerzock.commands.ExitCommand;
import net.villagerzock.commands.KickCommand;
import net.villagerzock.entities.EntityHandler;
import net.villagerzock.entities.EntityHandlerImpl;
import net.villagerzock.entities.Player;
import net.villagerzock.entities.PlayerEntity;
import net.villagerzock.event.Event;
import net.villagerzock.event.EventHandler;
import net.villagerzock.event.ServerTickEvent;
import net.villagerzock.item.Item;
import net.villagerzock.packet.Networker;
import net.villagerzock.packet.PacketByteBuffer;
import net.villagerzock.player.Armor;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.reflections.Reflections;

import java.awt.*;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class TotkServer extends Server {
    private static final TotkServer instance = new TotkServer();
    private final PluginLoader loader = new PluginLoader();
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final int TPS = 30;
    public DatagramSocket serverSocket;

    protected TotkServer() {
        super(new EntityHandlerImpl());
    }

    public static void main(String[] args) {
        boolean isHeadless = GraphicsEnvironment.isHeadless();
        if (!isHeadless){
            ImGuiServerHandler handler = new ImGuiServerHandler();
            handler.setupConsole();
            Thread thread = new Thread(()->{
                Application.launch(handler);
            },"ImGuiHandler");
            thread.start();
        }
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
        registerPackets();
        registerCommands();
        for (Plugin plugin : loader.getLoadedPlugins())
            plugin.onEnable();
        Timer timer = new Timer("Ticker");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
            }
        },0l,1000l / TPS);
        Timer t = new Timer("CommandScanner");
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                String command = scanner.nextLine();
                String[] arg = command.split(" ");
                String[] args = new String[arg.length-1];
                for (int i = 1; i < arg.length; i++) {
                    args[i-1] = arg[i];
                }
                try {
                    Command<?> cmd = Command.commands.get(arg[0]);
                    System.out.println(arg[0]);
                    System.out.println(arg[0]);
                    parseOptions(args,cmd);
                } catch (CmdLineException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0L, 10L);

        int port = 1027;
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            this.serverSocket = serverSocket;
            System.out.println(RED + "Server Starting on 0.0.0.0:" + port);
            System.out.println("Use https://whatismyipaddress.com/ to see your Online IP address" + RESET);
            while (true){
                PacketByteBuffer.UdpPacket received = PacketByteBuffer.receiveWithSender(serverSocket);
                PacketByteBuffer byteBuf = received.buffer();

                byte packet = byteBuf.readByte();
                if (packet == 0){
                    String name = byteBuf.readString();
                    Player p = new PlayerEntity(name,new Armor[4],new Item[0],received.address());
                    int i = 0;
                    for (Player player : getEntityHandler().getPlayers()){
                        PacketByteBuffer s2cBuf = new PacketByteBuffer();
                        s2cBuf.writeString(name);
                        s2cBuf.writeByte((byte) getEntityHandler().getPlayers().size());
                        Networker.sendPacket(player,s2cBuf);
                        PacketByteBuffer s2pBuf = new PacketByteBuffer();
                        s2pBuf.writeString(player.getName());
                        s2pBuf.writeByte((byte) i);
                        Networker.sendPacket(p,s2pBuf);
                        i++;
                    }
                    getEntityHandler().addPlayer(p);
                }
                Networker.receivedPacket(packet, getEntityHandler().getPlayer(received.address().hashCode()),byteBuf,received.address());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void registerCommands() {
        Command.commands.put("kick",new KickCommand());
        Command.commands.put("exit",new ExitCommand());
    }

    private <T> void parseOptions(String[] args,Command<T> command) throws CmdLineException {
        T options = command.getOptions();
        CmdLineParser parser = new CmdLineParser(options);
        parser.parseArgument(args);
        command.onExecute(options);
    }

    private void registerPackets() {
        Networker.registerClientPacket(this::playerJoinedPacket);
        Networker.registerClientPacket(this::playerDisconnectPacket);
        Networker.registerClientPacket(this::playerMovementPacket);
        Networker.registerClientPacket(this::playerDamagePacket);
        Networker.registerClientPacket(this::playerAttackPacket);
        Networker.registerClientPacket(this::playerChatMessagePacket);
        Networker.registerClientPacket(this::hartBeat);
    }

    private void hartBeat(Server server, Player player, PacketByteBuffer byteBuffer, InetAddress inetAddress) {
    }

    private void playerChatMessagePacket(Server server, Player player, PacketByteBuffer byteBuffer, InetAddress inetAddress) {
        System.out.println("[" + player.getName() + "] " + byteBuffer.readString());
    }

    private void playerAttackPacket(Server server, Player player, PacketByteBuffer byteBuffer, InetAddress inetAddress) {
    }

    private void playerDamagePacket(Server server, Player player, PacketByteBuffer byteBuffer, InetAddress inetAddress) {
    }

    private void playerMovementPacket(Server server, Player player, PacketByteBuffer byteBuffer, InetAddress inetAddress) {
    }

    private void playerDisconnectPacket(Server server, Player player, PacketByteBuffer byteBuffer, InetAddress inetAddress) {
    }

    private void playerJoinedPacket(Server server, Player player, PacketByteBuffer byteBuffer, InetAddress ip) {
        System.out.println(GREEN + player.getIPHash() + "/[" + player.getName() + "] has joined the game" + RESET);
    }

    public static final TotkServer getInstance(){
        return instance;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return loader;
    }

    @Override
    public void shutdown() {
        System.exit(0);
    }

    @Override
    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    private void tick(){
        //Necessary Tick Operations

        for (Player player : getEntityHandler().getPlayers()){
            for (int i = 0; i < getEntityHandler().getPlayers().size(); i++) {
                PacketByteBuffer byteBuf = new PacketByteBuffer();
                byteBuf.writeByte((byte) 1);
                byteBuf.writeByte((byte) i);
                byteBuf.writeVector3(player.getVelocity());
                byteBuf.writeFloat(player.getRotation().yaw());
                Networker.sendPacket(player,byteBuf);
            }
        }

        if (Event.emit(new ServerTickEvent(this)))
            return;
        //Cancelable Tick Operations
    }

}