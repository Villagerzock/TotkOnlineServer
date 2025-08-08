package net.villagerzock.player;

import net.villagerzock.item.Item;

public interface Armor extends Item {
    enum ArmorType {
        HEAD,
        CHEST,
        LEGS,
        ALL
    }
    String getID();
    ArmorType getArmorType();
}
