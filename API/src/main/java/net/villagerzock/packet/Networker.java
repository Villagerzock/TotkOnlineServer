package net.villagerzock.packet;

import net.villagerzock.Server;
import net.villagerzock.entities.Player;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Networker {
    private static Map<Byte, ClientPacketEvent> events = new HashMap<>();
    public static void registerClientPacket(ClientPacketEvent event){
        events.put((byte)events.size(),event);
    }
    public static void sendPacket(Player player, PacketByteBuffer byteBuffer){
        byteBuffer.send(Server.getInstance().getServerSocket(),player.getAddress(),1027);
    }
    public static void receivedPacket(byte name, Player player, PacketByteBuffer byteBuffer, InetAddress ip){
        //System.out.println(events.keySet().toArray()[name]);
        if (name >= events.size()){
            System.out.println("Unidentified Packet Received!");
        }
        ((ClientPacketEvent)events.values().toArray()[name]).receive(Server.getInstance(),player,byteBuffer,ip);
    }
}
