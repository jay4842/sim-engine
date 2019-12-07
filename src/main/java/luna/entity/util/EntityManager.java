package luna.entity.util;

import luna.entity.Entity;

import java.util.*;

public class EntityManager {

    public static Map<Integer, ArrayList<Integer>> entityRefMap;
    public static List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());

    public EntityManager(){
        entityRefMap = new HashMap<>();
    }

    public void update(int seconds){

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
