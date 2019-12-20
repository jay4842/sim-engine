package luna.world.util;

import luna.world.objects.Food;
import luna.world.objects.HostileEncounter;
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

    public static List<InteractableObject> interactableObjects = Collections.synchronizedList(new ArrayList<>());
    public static List<ItemRef> itemRefs = Collections.synchronizedList(new ArrayList<>());
    public static List<Item> items = Collections.synchronizedList(new ArrayList<>());
    private static ItemMaker itemMaker;


    public ObjectManager(){
        itemMaker = new ItemMaker();
    }

    // int xPos, int yPos, String type, int objectID,int world_h, int world_w, int world_scale
    public static int createObject(int xPos, int yPos, String type, int world_h, int world_w, int world_scale, boolean test){
        InteractableObject obj = null;
        if(type.contains("food")){
            type += "_" + interactableObjects.size();
            obj = new Food(xPos, yPos, type, interactableObjects.size(), world_h, world_w, world_scale);
        }else if(type.contains("hostile")){
            type += "_" + (interactableObjects.size()+1);
            obj = new HostileEncounter(xPos,yPos,type,interactableObjects.size()+1,world_h,world_w,world_scale, test);
        }else if(type.contains("resource")){
            type += "_" + interactableObjects.size();
            if(type.split("_")[1].equals("wood"))
                obj = new Resource(xPos, yPos, type, interactableObjects.size(), world_h, world_w, world_scale,Color.orange);
            if(type.split("_")[1].equals("stone"))
                obj = new Resource(xPos, yPos, type, interactableObjects.size(), world_h, world_w, world_scale,Color.gray);
        }

        if (obj != null) {
            System.out.println(obj.getType());
            interactableObjects.add(obj);
            return obj.getObjectID();
        }
        return -1;
    }//

    // for calling an item
    public static void modifyItem(int id, String cmd, int x){
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

    public static void addItem(Item item){
        items.add(item);
    }

    public static Item createItem(String type){
        return itemMaker.createItem(type);
    }
}
