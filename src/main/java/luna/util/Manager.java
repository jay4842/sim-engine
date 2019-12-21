package luna.util;

import luna.entity.Entity;
import luna.entity.util.EntityManager;
import luna.world.World;
import luna.world.util.ObjectManager;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

// TODO: encapsulate EntityManager, ObjectManager, and any other manager required into this class.
//  - Trying to reduce the amount of public static variables
//  - needed for testing too
//  - Manager will handle all updates for objects that are not directly bound to the World class
public class Manager {
    private EntityManager entityManager;
    private ObjectManager objectManager;


    public Manager(){
        entityManager = new EntityManager();
        objectManager = new ObjectManager();
    }


    public void update(List<List<Tile>> tileMap, int seconds){
        //

        if(seconds > 0 && seconds % (60*90) == 0){
            entityManager.update(seconds);
        }

        // update entities
        for (Entity tmp : entityManager.entities) {
            if (tmp != null) {
                if (tmp.getPosition() > -1) {
                    if (tmp.isAlive()) tmp.update(World.subMaps.get(tmp.getPosition()).getTileMap(), seconds);
                } else if (tmp.isAlive()) tmp.update(tileMap, seconds);
            }
            //
        }

        entityManager.checkGroups();
    }

    public void render(Graphics2D g){

        // render entities
        for (Entity tmp : entityManager.entities) {
            if (tmp != null) {
                if (tmp.getPosition() > -1) {
                    if (tmp.isAlive() && tmp.getPosition() == World.visibleMap) tmp.render(g);
                } else if (tmp.isAlive()) tmp.render(g);
            }
            //
        }
    }

    // modifiers
    public void addEntity(int x, int y, int width, int height, int world_scale, Color c, int count){
        entityManager.entities.add(new Entity(x,y,width,height,world_scale,c,count));
    }

    // for testing
    public void addEntity(int x, int y, int width, int height, int world_scale, Color c, int count, boolean test){
        entityManager.entities.add(new Entity(x,y,width,height,world_scale,c,count, test));
    }

    //
    public void shutdown(){

        for (Entity tmp : entityManager.entities) {
            if (tmp != null)
                tmp.shutdown();
        }
    }//

    public void editMapRef(String cmd, int pos, int id){
        entityManager.editRefMap(cmd,pos,id);
    }


    // every cmd will be separated by '_',
    // <request>_<variable/cmd>
    //  they will be similar to http requests, get, post, and maybe some others
    // This is going to get a little messy for sure, but it will be the majority of this class too
    //
    // x will always represent an index value for the initial call to the list
    public Object call(String key, int x){
        String [] keySplit = key.split("_");
        if(keySplit[0].equals("get")){
            switch (keySplit[1]){
                case "groupSize":{
                    if(x >= 0 && x < entityManager.groups.size())
                        return entityManager.groups.get(x).getEntitiesInGroup().size();
                    return -1;
                }
                case "entitySize":{
                    return entityManager.entities.size();
                }
                case "entity":{
                    if(x >= 0 && x < entityManager.entities.size())
                        return entityManager.entities.get(x);
                    return null;
                }
                case "group":{
                    if(x >= 0 && x < entityManager.groups.size())
                        return entityManager.groups.get(x);
                    return null;
                }
                case "EntityCheckInBondList":{
                    if(x >= 0 && x < entityManager.entities.size()){
                        return entityManager.entities.get(x).inBondList(Integer.parseInt(keySplit[2]));
                    }
                    return -1;
                }

                case "itemsInGroup":{
                    if(x >= 0 && x < entityManager.groups.size())
                        return entityManager.groups.get(x).getItemsInGroup();
                    return -1;
                }
            }
        }else if (keySplit[0].equals("post")){
            switch (keySplit[1]) {
                case "entityTakeDmg": {
                    if(x >= 0 && x < entityManager.entities.size()){
                        entityManager.entities.get(x).takeDmg(Integer.parseInt(keySplit[2]), Integer.parseInt(keySplit[3]));
                        return 1;
                    }
                    return -1;
                }
                case "lockEntity":{
                    if(x >= 0 && x < entityManager.entities.size()){
                        entityManager.entities.get(x).lockEntity(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "lockEntityDirection":{
                    if(x >= 0 && x < entityManager.entities.size()){
                        int dir = Integer.parseInt(keySplit[2]);
                        entityManager.entities.get(x).setLock(dir);
                        return 1;
                    }
                    return -1;
                }

                case "setEntityGroupId":{
                    if(x >= 0 && x < entityManager.entities.size()){
                        int groupId = Integer.parseInt(keySplit[2]);
                        entityManager.entities.get(x).setGroupId(groupId);
                        return 1;
                    }
                    return -1;
                }
                case "entityUpdateBond":{
                    if(x >= 0 && x < entityManager.entities.size()){
                        int bond = Integer.parseInt(keySplit[3]);
                        int callingEntity = Integer.parseInt(keySplit[2]);
                        int bondIdx = entityManager.entities.get(x).inBondList(callingEntity);
                        entityManager.entities.get(x).getBondList().get(bondIdx).updateBond(bond);
                        return 1;
                    }
                    return -1;
                }

                case "entityAddBond":{
                    if(x >= 0 && x < entityManager.entities.size()){
                        int newId = Integer.parseInt(keySplit[2]);
                        entityManager.entities.get(x).addBond(newId);
                        return 1;
                    }
                    return -1;
                }

                case "setEntityInteractTimer":{
                    if(x >= 0 && x < entityManager.entities.size()){
                        entityManager.entities.get(x).setInteractTimer(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "groupChangePosition":{
                    if(x >= 0 && x < entityManager.groups.size()){
                        entityManager.groups.get(x).groupChangePosition(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "completeGroupTask":{
                    if(x >= 0 && x < entityManager.groups.size()){
                        entityManager.groups.get(x).completeTask();
                        return 1;
                    }
                }

                case "removeEntityFromGroup":{
                    if(x >= 0 && x < entityManager.groups.size()){
                        int id = Integer.parseInt(keySplit[2]);
                        entityManager.groups.get(x).removeEntityFromList(id);
                        return 1;
                    }
                    return -1;
                }

                case "setGroupLeader":{
                    if(x >= 0 && x < entityManager.groups.size()) {
                        int id = Integer.parseInt(keySplit[2]);
                        entityManager.groups.get(x).setLeader(id);
                        return 1;
                    }
                    return -1;
                }

                case "addGroupMember":{
                    if(x >= 0 && x < entityManager.groups.size()) {
                        int id = Integer.parseInt(keySplit[2]);
                        entityManager.groups.get(x).addEntity(id);
                        return 1;
                    }
                    return -1;
                }

                case "setGroupBase":{
                    if(x >= 0 && x < entityManager.groups.size()) {
                        int yPos = Integer.parseInt(keySplit[2]);
                        int xPos = Integer.parseInt(keySplit[3]);
                        entityManager.groups.get(x).setBasePos(new int[]{yPos, xPos});
                        return 1;
                    }
                    return -1;
                }

                case "entityInteraction":{
                    if(x >= 0 && x < entityManager.groups.size()){
                        entityManager.entities.get(x).entityInteraction(Integer.parseInt(keySplit[2]));
                    }
                }
            }
        }
        return null;
    }
}// end of class

