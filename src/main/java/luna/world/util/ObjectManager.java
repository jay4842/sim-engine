package luna.world.util;

import luna.world.objects.Food;
import luna.world.objects.encounter.HostileEncounter;
import luna.world.objects.InteractableObject;
import luna.world.objects.Resource;
import luna.world.objects.item.Item;
import luna.world.objects.item.ItemMaker;
import luna.world.objects.item.ItemRef;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This guy will handle every type of object, from item to interactable object
public class ObjectManager {

    public List<InteractableObject> interactableObjects = Collections.synchronizedList(new ArrayList<>());
    public List<ItemRef> itemRefs = Collections.synchronizedList(new ArrayList<>());
    public List<Item> items = Collections.synchronizedList(new ArrayList<>());
    private ItemMaker itemMaker;


    public ObjectManager(){
        itemMaker = new ItemMaker();
    }

    // int xPos, int yPos, String type, int objectID,int world_h, int world_w, int world_scale
    public int createObject(TileParameters parms){
        InteractableObject obj = null;
        if(parms.type.contains("food")){
            parms.type += "_" + interactableObjects.size();
            obj = new Food(parms.xPos, parms.yPos, parms.type, interactableObjects.size(), parms.world_h, parms.world_w, parms.world_scale);
        }else if(parms.type.contains("hostile")){
            parms.type += "_" + (interactableObjects.size()+1);
            obj = new HostileEncounter(parms.xPos,parms.yPos,parms.type,interactableObjects.size()+1,parms.world_h,parms.world_w,parms.world_scale, parms.test);
        }else if(parms.type.contains("resource")){
            parms.type += "_" + interactableObjects.size();
            if(parms.type.split("_")[1].equals("wood"))
                obj = new Resource(parms.xPos, parms.yPos, parms.type, interactableObjects.size(), parms.world_h, parms.world_w, parms.world_scale,Color.orange);
            if(parms.type.split("_")[1].equals("stone"))
                obj = new Resource(parms.xPos, parms.yPos, parms.type, interactableObjects.size(), parms.world_h, parms.world_w, parms.world_scale,Color.gray);
        }

        if (obj != null) {
            System.out.println(obj.getType());
            interactableObjects.add(obj);
            return obj.getObjectID();
        }
        return -1;
    }//

    // for calling an item
    // = dep, moving this type of call to manager.java
    public void modifyItem(int id, String cmd, int x){
        int itemIdx = -1;
        for(int i = 0; i < items.size(); i++){
            if(id == items.get(i).getUniqueID()){
                itemIdx = i;
                break;
            }
        }
        //
        if(itemIdx >= 0){
            if(cmd.equals("addAmount")){
                items.get(itemIdx).addAmount(x);
            }else if(cmd.equals("remove")){
                items.remove(itemIdx);
            }else if(cmd.equals("setAmount")){
                items.get(itemIdx).setAmount(x);
            }
        }else{
            System.out.println("Error accessing item: Does not exist!");
        }
    }

    public void addItem(Item item){
        items.add(item);
    }

    public Item createItem(String type){
        return itemMaker.createItem(type);
    }

    public ItemMaker getItemMaker() {
        return itemMaker;
    }
}
