package luna.entity.util;

import luna.entity.Entity;
import luna.world.World;

import java.util.*;

public class EntityManager {

    public static Map<Integer, ArrayList<Integer>> entityRefMap;
    public static List<Entity> entities = Collections.synchronizedList(new ArrayList<>());
    public List<Group> groups = Collections.synchronizedList(new ArrayList<>());

    public EntityManager(){
        entityRefMap = new HashMap<>();
    }

    public void update(int seconds){
        // first remove any entities that are not alive
        for(int i = 0; i < entities.size(); i++){
            if(!entities.get(i).isAlive()){
                entities.remove(i);
                i--;
            }
        }//
        //now we reassign entity ids
        for(int i = 0; i < entities.size(); i++){
            if(entities.get(i).getEntityID() != i){
                entities.get(i).changeEntityID(i);
            }
        }// that should be it
        // update every ref
        for(int i = 0; i < World.subMaps.size(); i++){
            World.subMaps.get(i).makeEntityRefs();
        }
        try{
            Thread.sleep(100);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void editRefMap(String cmd, int pos, int id){
        if(cmd.equals("add")){
            // first we need to check if the key they wish to add exists
            if(entityRefMap.containsKey(pos))
                entityRefMap.get(pos).add(id);
            else{ // need to init the array list
                entityRefMap.put(pos, new ArrayList<>());
                entityRefMap.get(pos).add(id);
            }
        }//
        else if(cmd.equals("remove")){
            if(entityRefMap.containsKey(pos)){
                for(int i = 0; i < entityRefMap.get(pos).size(); i++){
                    if(entityRefMap.get(pos).get(i) == id){
                        entityRefMap.get(pos).remove(i);
                        break;
                    }
                }
            }else{
                System.out.println("Key does not exist");
            }
        }else{
            System.out.println("Invalid cmd. valid cmds -> [add, remove]");
        }

        // print map for now
        /*for(int key : entityRefMap.keySet()){
            for(int i : entityRefMap.get(key)){
                System.out.println("key [" + key + "] - " + i);
            }
        }
        */
    }

    public static Map<Integer, ArrayList<Integer>> getEntityRefMap() {
        return entityRefMap;
    }

}
