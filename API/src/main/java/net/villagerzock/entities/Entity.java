package net.villagerzock.entities;

import net.villagerzock.GameObject;

public interface Entity extends GameObject {
    int getHealth();
    int getMaxHealth();
}
