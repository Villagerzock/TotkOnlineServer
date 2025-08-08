package net.villagerzock.packet;

import javassist.bytecode.ByteArray;
import net.villagerzock.math.Quaternion;
import net.villagerzock.math.Vector3;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketByteBuffer {
    private final DataInputStream dataInputStream;
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final DataOutputStream dataOut = new DataOutputStream(baos);
    public PacketByteBuffer(ByteArrayInputStream stream) {
        this.dataInputStream = new DataInputStream(stream);
    }
    public int remaining(){
        try {
            return dataInputStream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isEmpty(){
        try {
            return dataInputStream.available() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public record UdpPacket(PacketByteBuffer buffer, InetAddress address, int port) {}

    public static UdpPacket receiveWithSender(DatagramSocket socket) {
        try {
            byte[] buf = new byte[65535];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            PacketByteBuffer buffer = new PacketByteBuffer(bais);
            return new UdpPacket(buffer, packet.getAddress(), packet.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static PacketByteBuffer receive(DatagramSocket socket) {
        try {
            byte[] buf = new byte[65535]; // max UDP-Größe
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet); // blockiert

            // Nur relevante Bytes extrahieren
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            return new PacketByteBuffer(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public PacketByteBuffer(){
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        dataInputStream = new DataInputStream(bais);
    }
    public String readString(){
        try {
            int length = dataInputStream.readInt();
            byte[] stringBytes = new byte[length];
            dataInputStream.readFully(stringBytes);
            return new String(stringBytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public int readInt(){
        try {
            return dataInputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public float readFloat(){
        try {
            return dataInputStream.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public double readDouble(){
        try {
            return dataInputStream.readDouble();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public byte readByte(){
        try {
            return dataInputStream.readByte();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Vector3 readVector3(){
        return new Vector3(readFloat(),readFloat(),readFloat());
    }
    public Quaternion readQuaternion(){
        return new Quaternion(readFloat(),readFloat(),readFloat(),readFloat());
    }
    public void writeString(String s){
        try {
            byte[] stringBytes = s.getBytes(StandardCharsets.UTF_8);
            dataOut.writeInt(stringBytes.length);
            dataOut.write(stringBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeInt(int i) {
        try {
            dataOut.writeInt(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeFloat(float i){
        try {
            dataOut.writeFloat(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeDouble(double i){
        try {
            dataOut.writeDouble(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeByte(byte i){
        try {
            dataOut.writeByte(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void writeVector3(Vector3 vec3){
        writeFloat(vec3.x);
        writeFloat(vec3.y);
        writeFloat(vec3.z);
    }
    public void writeQuaternion(Quaternion quat){
        writeFloat(quat.x);
        writeFloat(quat.y);
        writeFloat(quat.z);
        writeFloat(quat.w);
    }
    public void send(DatagramSocket socket, InetAddress address, int port) {
        try {
            byte[] data = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
