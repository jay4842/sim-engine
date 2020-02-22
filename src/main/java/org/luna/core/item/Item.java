package org.luna.core.item;

public class Item {
    private static int counter = 0;
    private int uniqueID;
    private int itemRefID;
    private int durability;
    private int amount;
    private String namespace;

    public Item(int ID, String namespace){
        counter++;
        itemRefID = ID;
        durability = -1;
        amount = 1; // will be 1 unless specified
        this.namespace = namespace;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public int getItemID() {
        return itemRefID;
    }

    public int getItemRefID() {
        return itemRefID;
    }

    public void setAmount(int a){
        this.amount = a;
    }

    public int getAmount(){
        return this.amount;
    }

    public void addAmount(int a){
        this.amount += a;
    }

    public String toString(){
        return "ItemRefID: " + getItemID() + " uniqueItemID: " + getUniqueID() + " "
                + getNamespace() + " amount: " + getAmount();
    }

    public String getNamespace() {
        return namespace;
    }

    public static int getCounter() {
        return counter;
    }
}
