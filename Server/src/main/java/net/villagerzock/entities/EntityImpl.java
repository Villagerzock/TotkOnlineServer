package net.villagerzock.entities;

import net.villagerzock.SimpleGameObjectImpl;

public class EntityImpl extends SimpleGameObjectImpl implements Entity{
    private int health;
    private final int maxHealth;

    public EntityImpl(int maxHealth) {
        this.maxHealth = maxHealth;
        health = maxHealth;
    }

    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public int getMaxHealth() {
        return 0;
    }
}
