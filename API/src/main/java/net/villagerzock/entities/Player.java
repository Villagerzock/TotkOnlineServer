package net.villagerzock.entities;

import net.villagerzock.item.Item;
import net.villagerzock.player.Armor;

import java.net.InetAddress;

public interface Player extends Entity{
    String getName();
    int getIPHash();
    Armor[] getArmor();
    Item selectedWeapon();
    Item selectedShield();
    Item selectedBow();
    Item selectedFuseItem();
    Item[] selectedItemInHand();
    InetAddress getAddress();
    void kick();
    void ban();
}
