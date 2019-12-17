package luna.world.util;

import luna.world.objects.Food;
import luna.world.objects.HostileEncounter;
import luna.world.objects.InteractableObject;
import luna.world.objects.item.Item;
import luna.world.objects.item.ItemRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This guy will handle every type of object, from item to interactable object
public class ObjectManager {

    public static List<InteractableObject> interactableObjects = Collections.synchronizedList(new ArrayList<>());
    public static List<ItemRef> itemRefs = Collections.synchronizedList(new ArrayList<>());
    public static List<Item> items = Collections.synchronizedList(new ArrayList<>());



    public ObjectManager(){

    }

    // int xPos, int yPos, String type, int objectID,int world_h, int world_w, int world_scale
    public static int createObject(int xPos, int yPos, String type, int world_h, int world_w, int world_scale){
        type += "_" + interactableObjects.size();
        System.out.println(type);
        if(type.contains("food")){
            Food f = new Food(xPos, yPos, type, interactableObjects.size(), world_h, world_w, world_scale);
            interactableObjects.add(f);
            return interactableObjects.size()-1;
        }else if(type.contains("hostile")){
            HostileEncounter h = new HostileEncounter(xPos,yPos,type,interactableObjects.size(),world_h,world_w,world_scale);
            interactableObjects.add(h);
            return interactableObjects.size()-1;
        }
        return -1;
    }
}
