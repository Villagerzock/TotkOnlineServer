package net.villagerzock.entities;

import net.villagerzock.SimpleGameObjectImpl;
import net.villagerzock.item.Item;
import net.villagerzock.math.Quaternion;
import net.villagerzock.math.Vector3;
import net.villagerzock.player.Armor;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class PlayerEntity extends SimpleGameObjectImpl implements Player{
    private final String playername;
    private final Armor[] armor;
    private final Item[] selectedItems;
    private int health = 3 * 4;
    private int maxHealth = 3;
    private float stamina;
    private int maxStamina;
    private final InetAddress address;

    public PlayerEntity(String playername, Armor[] armor, Item[] selectedItems, InetAddress address) {
        this.playername = playername;
        this.armor = armor;
        this.selectedItems = selectedItems;
        this.address = address;
        setPosition(new Vector3(343f,-1755f,2280f));
        setRotation(new Quaternion());
    }

    @Override
    public String getName() {
        return playername;
    }

    @Override
    public int getIPHash() {
        return address.hashCode();
    }

    @Override
    public Armor[] getArmor() {
        return armor;
    }

    @Override
    public Item selectedWeapon() {
        return selectedItems[5];
    }

    @Override
    public Item selectedShield() {
        return selectedItems[6];
    }

    @Override
    public Item selectedBow() {
        return selectedItems[7];
    }

    @Override
    public Item selectedFuseItem() {
        return selectedItems[8];
    }

    @Override
    public Item[] selectedItemInHand() {
        return Arrays.copyOf(selectedItems,5);
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public void kick() {

    }

    @Override
    public void ban() {

    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }
    public float getStamina() {
        return stamina;
    }
    public int getMaxStamina(){
        return maxStamina;
    }

}
