package luna.world.objects.item;

import luna.util.Util;

import java.util.ArrayList;
import java.util.List;

// Will be used as a reference for items based on type
public class ItemRef {
    private static int counter = 0;
    private String name;
    private String type;
    private String imagePath;
    private int itemID; // will link it to the interactable object
    private List<String> properties;

    public ItemRef(){
        itemID = counter;
        counter++;
        this.imagePath = "none";
        this.type = "none";
        this.name = "none";
        properties = new ArrayList<>();

    }

    public ItemRef(String type, String name){
        itemID = counter;
        counter++;
        this.type = type;
        this.name = name;
        properties = new ArrayList<>();
    }

    public ItemRef(String type, String name, ArrayList<String> properties){
        itemID = counter;
        counter++;
        this.type = type;
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNamespace(){
        return type + "_" + name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public String toString(){
        return "name : " + getName() + "\n" +
               "type : " + getType() + "\n" +
               "props: " + Util.makeArrString(getProperties().toArray()) + "\n";
    }
}
