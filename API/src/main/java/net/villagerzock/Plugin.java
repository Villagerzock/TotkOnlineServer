package net.villagerzock;

public interface Plugin {
    default void onLoad() {}
    void onEnable();

}
