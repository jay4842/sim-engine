package luna.world.objects.item;

import java.util.ArrayList;
import java.util.List;

// Will be used as a reference for items based on type
public class ItemRef {
    private static int counter = 0;
    private String type;
    private int itemID; // will link it to the interactable object
    private List<String> properties;

    public ItemRef(String type){
        itemID = counter;
        counter++;
        properties = new ArrayList<>();
    }

    public ItemRef(String type, ArrayList<String> properties){
        itemID = counter;
        counter++;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public int getItemID() {
        return itemID;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void addProperty(String prop){
        this.properties.add(prop);
    }
}
