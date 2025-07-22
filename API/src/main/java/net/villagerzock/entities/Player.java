package net.villagerzock.entities;

import net.villagerzock.item.Item;
import net.villagerzock.player.Armor;

public interface Player extends Entity{
    String getName();
    String getIPHash();
    Armor[] getArmor();
    Item selectedWeapon();
    Item selectedShield();
    Item selectedBow();
    Item selectedFuseItem();
    Item[] selectedItemInHand();
}
