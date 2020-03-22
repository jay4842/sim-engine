package org.luna.core.object;

import org.json.simple.JSONObject;
import org.luna.core.util.Utility;

import java.awt.*;

public class WorldObject {

    private static int counter = 0;
    private int listId;
    private int uniqueId;
    private int[] gps;
    private int type;
    private boolean active;
    private int size;
    private int itemRefId;

    public WorldObject(){
        listId = -1;
        uniqueId = counter;
        counter++;
        gps = new int[3];
        type = 0;
        active = true;
        size = 0;
        itemRefId = -1;
    }

    public WorldObject(int listId, int[] gps, int type, int size){
        this.listId = listId;
        this.gps = gps;
        this.type = type;
        this.size = size;
        active = true;
        uniqueId = counter;
        itemRefId = -1;
        counter++;
    }

    public WorldObject(int listId, int[] gps, int type, int size, int itemRefId){
        this.listId = listId;
        this.gps = gps;
        this.type = type;
        this.size = size;
        active = true;
        uniqueId = counter;
        this.itemRefId = itemRefId;
        counter++;
    }

    public void update(int step, int x){
        //
    }

    public void render(int x, Graphics2D g){
        //
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int[] getGps() {
        return gps;
    }

    public int getType() {
        return type;
    }

    public static int getCounter() {
        return counter;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int id){
        this.listId = id;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public int getSize() {
        return size;
    }

    // each world object will have a way to describe itself, and it can change (might make it just a string)
    public Object getDescription(){
        return -1;
    }

    public String toString(){
        JSONObject obj = new JSONObject();
        obj.put("type", getType());
        obj.put("gps", Utility.arrayToJSONArray(gps));
        return obj.toString();
    }

    public int getItemRefId() {
        return itemRefId;
    }
}

