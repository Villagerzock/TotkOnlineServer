package net.villagerzock.item;

public interface Item {
    enum InventoryStash {
        WEAPONS("weapons"),
        SHIELDS("shields"),
        BOW("bow"),
        ARMOR("armo"),
        MATERIAL("material"),
        FOOD("food"),
        ZONAI_MACHINES("zonai"),
        KEY("key")
        ;
        private final String id;
        InventoryStash(String id){
            this.id = id;
        }

        public String getID() {
            return id;
        }
    }
    String getID();
    InventoryStash getStash();
}
