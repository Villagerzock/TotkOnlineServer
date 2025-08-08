package net.villagerzock.packet;

import net.villagerzock.Server;
import net.villagerzock.entities.Player;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public interface ClientPacketEvent {
    void receive(Server instance, Player sender, PacketByteBuffer byteBuf, InetAddress ipAdress);
}
