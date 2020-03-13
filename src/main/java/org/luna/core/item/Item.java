package org.luna.core.item;

public class Item {
    private static int counter = 0;
    private int listID;
    private int uniqueID;
    private int itemRefID;
    private int durability;
    private int amount;
    private String namespace;
    private int simId;

    public Item(int ID, String namespace, int sim){
        this.uniqueID = counter;
        counter++;
        itemRefID = ID;
        listID = -1;
        durability = -1;
        amount = 1; // will be 1 unless specified
        this.namespace = namespace;
        this.simId = sim;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
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
        System.out.println("item " + getUniqueID() + " curr amount " + getAmount());
        this.amount = a;
        System.out.println("item " + getUniqueID() + " udt amount " + getAmount());
    }

    public void subAmount(int sub){
        this.amount -= sub;
    }

    public int getAmount(){
        return this.amount;
    }

    public void addAmount(int a){
        this.amount += a;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String toString(){
        return "sim:" + simId + ",ItemRefID:" + getItemID() + ",uniqueItemID:" + getUniqueID() + ",namespace:"
                + getNamespace() + ",amount: " + getAmount();
    }

    public String getNamespace() {
        return namespace;
    }

    public static int getCounter() {
        return counter;
    }

    @Override
    public boolean equals(Object other){
        if (!(other instanceof Item))
            return false;
        Item otherItem = (Item) other;
        return getUniqueID() != otherItem.getUniqueID();
    }

    @Override
    public int hashCode() {
        return getUniqueID();
    }
}
