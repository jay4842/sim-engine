package luna.world.objects.item;

import luna.world.World;

/*
* Items will be used mainly by interactable objects, it will use itemRef to call description and other static properties
* the only difference is that items can have durability. if it is not -1, the items can degrade overtime like food
*
* */
public class Item {
    private static int counter = 0;
    private int uniqueID;
    private int itemRefID;
    private int durability;
    private int amount;

    public Item(int ID){
        counter++;
        itemRefID = ID;
        durability = -1;
        amount = 1; // will be 1 unless specified
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

    public void setAmount(int a){
        this.amount = a;
    }

    public int getAmount(){
        return this.amount;
    }

    public ItemRef getRef(){
        return (ItemRef) World.callManager("get_itemRef", itemRefID);
    }

    public void addAmount(int a){
        this.amount += a;
    }

    public String toString(){
        return "ItemRefID: " + getItemID() + " uniqueItemID: " + getUniqueID() + " "
                + getRef().getNamespace() + " amount: " + getAmount();
    }

    public static int getCounter() {
        return counter;
    }
}
