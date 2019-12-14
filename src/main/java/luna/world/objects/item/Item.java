package luna.world.objects.item;

import java.util.List;

/*
* Items will be used mainly by interactable objects, it will use itemRef to call description and other static properties
* the only difference is that items can have durability. if it is not -1, the items can degrade overtime like food
*
* */
public class Item {
    private static int counter = 0;
    private int uniqueID;
    private int itemID;
    private int durability;

    public Item(int ID){
        uniqueID = counter;
        counter++;
        itemID = ID;
        durability = -1;
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
        return itemID;
    }
}
