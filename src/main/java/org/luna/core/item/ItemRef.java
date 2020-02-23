package org.luna.core.item;

import jdk.jshell.execution.Util;
import org.luna.core.util.ImageUtility;
import org.luna.core.util.Utility;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ItemRef {
    private static int counter = 0;
    private String name;
    private String type;
    private String imagePath;
    private int itemID; // will link it to the interactable object
    private List<String> properties;
    private BufferedImage itemImage;
    private int[] amtBounds;

    public ItemRef(){
        itemID = counter;
        counter++;
        this.imagePath = "none";
        this.type = "none";
        this.name = "none";
        properties = new ArrayList<>();
        this.amtBounds = new int[]{1,1};
    }

    public ItemRef(String type, String name){
        itemID = counter;
        counter++;
        this.type = type;
        this.name = name;
        properties = new ArrayList<>();
        this.amtBounds = new int[]{1,1};
    }

    public ItemRef(String type, String name, ArrayList<String> properties){
        itemID = counter;
        counter++;
        this.type = type;
        this.name = name;
        this.properties = properties;
        this.amtBounds = new int[]{1,1};
    }

    public ItemRef(String type, String name, ArrayList<String> properties, int[] amtBounds){
        itemID = counter;
        counter++;
        this.type = type;
        this.name = name;
        this.properties = properties;
        this.amtBounds = amtBounds;
    }

    public void loadImage(String path){
        itemImage = ImageUtility.load(path);
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
        return "name   : " + getName() + "\n" +
                "type   : " + getType() + "\n" +
                "props  : " + Utility.makeArrString(getProperties().toArray()) + "\n" +
                "amount : " + Utility.makeArrString(getAmtBounds()) + "\n";
    }

    public BufferedImage getItemImage() {
        return itemImage;
    }

    public int[] getAmtBounds() {
        return amtBounds;
    }

    public void setAmtBounds(int[] amtBounds) {
        this.amtBounds = amtBounds;
    }
}
