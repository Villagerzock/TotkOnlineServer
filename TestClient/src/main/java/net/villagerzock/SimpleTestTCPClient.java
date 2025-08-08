package net.villagerzock;

import net.villagerzock.packet.Networker;
import net.villagerzock.packet.PacketByteBuffer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleTestTCPClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your name:");
        String l = scanner.nextLine();
        while (l.isEmpty()){
            l=scanner.nextLine();
        }
        String host = l;
        l=scanner.nextLine();
        while (l.isEmpty()){
            l=scanner.nextLine();
        }
        int port = 1027;
        try (DatagramSocket socket = new DatagramSocket(1026)) {
            InetAddress serverAddress = InetAddress.getByName(host);
            PacketByteBuffer byteBuffer = new PacketByteBuffer();

            byteBuffer.writeByte((byte)0);
            byteBuffer.writeString(l);
            byteBuffer.send(socket, serverAddress,port);

            Timer timer = new Timer("Heartbeat");
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("BopBom");
                    if (socket.isClosed()){
                        System.out.println("Socket Closed, Stopped Hartbeat!");
                        cancel();
                    }
                    PacketByteBuffer byteBuf = new PacketByteBuffer();
                    byteBuf.writeByte((byte) 6);
                    byteBuf.send(socket,serverAddress,port);
                }
            },0L,10000L);

            while(!socket.isClosed()){
                String line = scanner.nextLine();
                if (!line.isEmpty()){
                    if (line == "exit"){
                        socket.close();
                        break;
                    }
                    PacketByteBuffer byteBuf = new PacketByteBuffer();
                    byteBuf.writeByte((byte) 5);
                    byteBuf.writeString(line);
                    byteBuf.send(socket,serverAddress,port);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
