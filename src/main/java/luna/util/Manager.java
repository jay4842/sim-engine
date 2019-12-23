package luna.util;

import luna.entity.Entity;
import luna.entity.util.EntityManager;
import luna.entity.util.Group;
import luna.world.World;
import luna.world.objects.InteractableObject;
import luna.world.objects.item.Item;
import luna.world.objects.item.ItemRef;
import luna.world.util.ObjectManager;
import luna.world.util.TileParameters;

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


    public void createItemRefs(){
        objectManager.getItemMaker().createItemRefs();
    }

    // every cmd will be separated by '_',
    // <request>_<variable/cmd>
    //  they will be similar to http requests, get, post, and maybe some others
    // This is going to get a little messy for sure, but it will be the majority of this class too
    //
    // x will always represent an index value for the initial call to the list
    public Object call(String key, Object x){
        //System.out.println("call: " + key);
        String [] keySplit = key.split("_");
        if(keySplit[0].equals("get")){
            switch (keySplit[1]){
                case "groupSize":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size())
                        return entityManager.groups.get((int)x).getEntitiesInGroup().size();
                    return -1;
                }
                case "entitySize":{
                    return entityManager.entities.size();
                }
                case "entity":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size())
                        return entityManager.entities.get((int)x);
                    return null;
                }
                case "group":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size())
                        return entityManager.groups.get((int)x);
                    return null;
                }
                case "EntityCheckInBondList":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        return entityManager.entities.get((int)x).inBondList(Integer.parseInt(keySplit[2]));
                    }
                    return -1;
                }

                case "itemsInGroup":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size())
                        return entityManager.groups.get((int)x).getItemsInGroup();
                    return -1;
                }

                case "itemsOnPerson":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        return entityManager.entities.get((int)x).getItemsOnPerson();
                    }
                    return -1;
                }

                case "object":{
                    if((int)x >= 0 && (int)x < objectManager.interactableObjects.size()){
                        return objectManager.interactableObjects.get((int)x);
                    }
                }

                case "item":{
                    if((int)x >= 0 && (int)x < objectManager.items.size()){
                        return objectManager.items.get((int)x);
                    }
                }

                case "itemsSize":{
                    return objectManager.items.size();
                }

                case "itemRef":{
                    if((int)x >= 0 && (int)x < objectManager.itemRefs.size()){
                        return objectManager.itemRefs.get((int)x);
                    }
                }

                case "itemRefSize":{
                    return objectManager.itemRefs.size();
                }

                case "itemRefs":{
                    return objectManager.itemRefs;
                }

                case "entityRefMap":{
                    return entityManager.getEntityRefMap();
                }

                case "entityTaskLogger":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        return entityManager.entities.get((int)x).getTaskLogger();
                    }
                }

            }
        }else if (keySplit[0].equals("post")){
            switch (keySplit[1]) {
                case "entityTakeDmg": {
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        entityManager.entities.get((int)x).takeDmg(Integer.parseInt(keySplit[2]), Integer.parseInt(keySplit[3]));
                        return 1;
                    }
                    return -1;
                }
                case "lockEntity":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        entityManager.entities.get((int)x).lockEntity(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "lockEntityDirection":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        int dir = Integer.parseInt(keySplit[2]);
                        entityManager.entities.get((int)x).setLock(dir);
                        return 1;
                    }
                    return -1;
                }

                case "setEntityGroupId":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        int groupId = Integer.parseInt(keySplit[2]);
                        entityManager.entities.get((int)x).setGroupId(groupId);
                        return 1;
                    }
                    return -1;
                }
                case "entityUpdateBond":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        int bond = Integer.parseInt(keySplit[3]);
                        int callingEntity = Integer.parseInt(keySplit[2]);
                        int bondIdx = entityManager.entities.get((int)x).inBondList(callingEntity);
                        entityManager.entities.get((int)x).getBondList().get(bondIdx).updateBond(bond);
                        return 1;
                    }
                    return -1;
                }

                case "entityAddBond":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        int newId = Integer.parseInt(keySplit[2]);
                        entityManager.entities.get((int)x).addBond(newId);
                        return 1;
                    }
                    return -1;
                }

                case "setEntityInteractTimer":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        entityManager.entities.get((int)x).setInteractTimer(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "entityChangePosition":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()){
                        entityManager.entities.get((int)x).changePosition(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "entitySetPosition":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()){
                        entityManager.entities.get((int)x).setPosition(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "groupChangePosition":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()){
                        System.out.println("changing group " + x + " pos to " + Integer.parseInt(keySplit[2]));
                        entityManager.groups.get((int)x).groupChangePosition(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "completeGroupTask":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()){
                        entityManager.groups.get((int)x).completeTask();
                        return 1;
                    }
                }

                case "removeEntityFromGroup":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()){
                        int id = Integer.parseInt(keySplit[2]);
                        entityManager.groups.get((int)x).removeEntityFromList(id);
                        return 1;
                    }
                    return -1;
                }

                case "setGroupLeader":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()) {
                        int id = Integer.parseInt(keySplit[2]);
                        entityManager.groups.get((int)x).setLeader(id);
                        return 1;
                    }
                    return -1;
                }

                case "addGroupMember":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()) {
                        int id = Integer.parseInt(keySplit[2]);
                        entityManager.groups.get((int)x).addEntity(id);
                        return 1;
                    }
                    return -1;
                }

                case "setGroupBase":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()) {
                        int yPos = Integer.parseInt(keySplit[2]);
                        int xPos = Integer.parseInt(keySplit[3]);
                        entityManager.groups.get((int)x).setBasePos(new int[]{yPos, xPos});
                        return 1;
                    }
                    return -1;
                }

                case "entityInteraction":{
                    if((int)x >= 0 && (int)x < entityManager.groups.size()){
                        entityManager.entities.get((int)x).entityInteraction(Integer.parseInt(keySplit[2]));
                    }
                }

                case "addGroup":{
                    int id = 0;
                    entityManager.groups.add(new Group());
                    id = entityManager.groups.size();
                    if(id > 0)id--;
                    return id;
                }

                case "setEntityJob":
                case "setEntityFocus": {
                    String focus = keySplit[2];
                    if((int)x >= 0 && (int)x < entityManager.groups.size()){
                        entityManager.entities.get((int)x).setFocus(focus);
                        return 1;
                    }
                    return -1;
                }

                case "harvestObject":{
                    if((int)x >= 0 && (int)x < objectManager.interactableObjects.size()){
                        return objectManager.interactableObjects.get((int)x).harvest();
                    }
                    return -1;
                }

                case "addAmountToItem":
                case "addItemAmount":{
                    if((int)x >= 0 && (int)x < objectManager.items.size()){
                        int amount = Integer.parseInt(keySplit[2]);
                        objectManager.items.get((int)x).addAmount(amount);
                        return 1;
                    }
                    return -1;
                }

                case "setItemAmount":{
                    if((int)x >= 0 && (int)x < objectManager.items.size()){
                        int amt = Integer.parseInt(keySplit[2]);
                        objectManager.items.get((int)x).setAmount(amt);
                        return 1;
                    }
                    return -1;
                }

                case "addItem": {
                    objectManager.items.add((Item)x);
                    return 1;
                }

                case "makeItem":
                case "createItem":{
                    return objectManager.createItem((String)x);
                }

                case "addItemRef":{
                    ItemRef ref = (ItemRef)x;
                    //System.out.println("addItemRef");
                    //System.out.println(ref.toString());
                    return objectManager.itemRefs.add(ref);
                }

                case "addEntity":{
                    if(x != null){
                        entityManager.entities.add((Entity)x);
                        return 1;
                    }
                    return -1;
                }

                case "entityEat":{
                    int idx = Integer.parseInt(keySplit[2]);
                    if(idx >= 0 && idx < entityManager.entities.size()){
                        entityManager.entities.get(idx).eat((InteractableObject) x);
                        return 1;
                    }
                    return -1;
                }

                case "entityRestoreHp":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        entityManager.entities.get((int)x).restoreHp();
                        return 1;
                    }
                    return -1;
                }

                case "entityAddXp":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        entityManager.entities.get((int)x).addXp(Integer.parseInt(keySplit[2]));
                        return 1;
                    }
                    return -1;
                }

                case "entityAddItem":{
                    int idx = Integer.parseInt(keySplit[2]);
                    if(idx >= 0 && idx < entityManager.entities.size()){
                        return entityManager.entities.get(idx).addItem((Item)x);
                    }
                }

                case "entitySetSubYX":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        int yPos = Integer.parseInt(keySplit[2]);
                        int xPos = Integer.parseInt(keySplit[3]);
                        entityManager.entities.get((int)x).setSubX(xPos);
                        entityManager.entities.get((int)x).setSubY(yPos);
                        return 1;
                    }
                    return -1;
                }

                case "entitySetDirection":{
                    if((int)x >= 0 && (int)x < entityManager.entities.size()){
                        int dir = Integer.parseInt(keySplit[2]);
                        entityManager.entities.get((int)x).setDirection(dir);
                        return 1;
                    }
                    return -1;
                }

                case "createObject":{
                    TileParameters parms = (TileParameters)x;
                    return objectManager.createObject(parms);
                }

            }
        }
        return null;
    }
}// end of class

