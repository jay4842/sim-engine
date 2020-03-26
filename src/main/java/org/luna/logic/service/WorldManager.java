package org.luna.logic.service;

import org.luna.core.item.Item;
import org.luna.core.map.LunaMap;
import org.luna.core.object.WorldObject;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

// Hold all of the maps in the world
public class WorldManager implements Manager{

    // sense objects belong to the world, it will belong to the world manager as well
    private EntityManager entityManager;
    private ItemManager itemManager;

    private List<LunaMap> mapList;
    private int visibleMap = 0;
    private int h, w, scale;
    private int turnStep;
    private int simId;
    private int currDay;
    private int lastDay;
    private int daySize;

    public WorldManager(int HEIGHT, int WIDTH, int world_scale, int turnStep, int simId){
        this.simId = simId;
        System.out.println("Making world manager");
        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;
        this.turnStep = turnStep;

        int size = HEIGHT/world_scale;
        // lets make some maps
        mapList = new ArrayList<>();

        LunaMap overWorld = new LunaMap(HEIGHT, WIDTH, world_scale, size, 0, simId);
        mapList.add(overWorld);

        // now populate other managers
        entityManager = new EntityManager(HEIGHT, WIDTH, world_scale, mapList.size(), turnStep, simId);
        itemManager = new ItemManager(simId);
        itemManager.createItemRefs();

        currDay = 0;
        lastDay = 0;
        daySize = turnStep * 24; // 1 step = 1 hour
    }

    @Override
    public List<ManagerCmd> update(int step, int n) {
        mapList.get(visibleMap).update(step, turnStep);
        List<ManagerCmd> cmds = entityManager.update(step, visibleMap, mapList.get(n), daySize);
        itemManager.update(step, n);
        // parse cmds
        for(ManagerCmd cmd : cmds){
            if(cmd.getCmd().contains("REMOVE")){
                String[] split = cmd.getCmd().split(",");
                if(cmd.getCmd().contains("OBJECT")){
                    int y = Integer.parseInt(split[3])/scale;
                    int x = Integer.parseInt(split[4])/scale;
                    int idx = Integer.parseInt(split[5]);
                    int entity_idx = Integer.parseInt(split[0]);
                    // here, if the object is a resource, return a resource item to the correct entity
                    WorldObject obj = mapList.get(n).getObject(y,x,idx);
                    //TODO: what happens when a removeObject is false?
                    System.out.println("From worldManager: call(removeObject[" + y + "," + x + "," + idx + "]): " + mapList.get(n).removeObject(y,x,idx));
                    if(obj.getItemRefId() != -1 && cmd.getCmd().contains("SAVE")){
                        Item item = itemManager.createItem(obj.getItemRefId());
                        String addItemCmd = entityManager.addItemToEntity(entity_idx, item);
                        if(addItemCmd.contains("REMOVE")){
                            String[] subSplit = addItemCmd.split(",");
                            int itemId = Integer.parseInt(subSplit[3]);
                            itemManager.destroyItem(itemId, step);
                            System.out.println("Entity(" + subSplit[0] + ") added to an existing item amount");
                        }
                        //itemManager.getItems().get(item.getUniqueID());
                    }
                }
                else if(cmd.getCmd().contains("ITEM")){
                    System.out.println(cmd.getCmd());
                    int itemId = Integer.parseInt(split[3]);
                    itemManager.destroyItem(itemId, step);
                }

            }
            else if(cmd.getCmd().contains("DROP")){
                //TODO: dropping items
                System.out.println("placeholder for drop commands");
            }
            else if(cmd.getCmd().contains("UPDATE")){
                String[] split = cmd.getCmd().split(",");
                if(cmd.getCmd().contains("ITEM")){
                    // get item idx
                    int itemId = Integer.parseInt(split[3]);
                    // update accordingly
                    if(cmd.getCmd().contains("AMOUNT")){
                        System.out.println("current item <" + itemId + "> amount to:" + itemManager.getItems().get(itemId).getAmount());
                        int amount = Integer.parseInt(split[5]);
                        itemManager.updateItem(itemId, "AMOUNT", amount);
                        System.out.println("updated item <" + itemId + "> amount to:" + itemManager.getItems().get(itemId).getAmount());
                    }

                // <entity_id>,UPDATE,ENTITY,INTERACT,<target_entity_id>,<interact_value>
                }else if(cmd.getCmd().contains("ENTITY")){
                    // TODO
                    System.out.println("placeholder for entity update");
                }
            }
        }
        // update days
        if(step % daySize == 0){
            lastDay = currDay;
            currDay++;
            System.out.print("last day " + lastDay);
            System.out.print("| curr day " + currDay + "\n");
        }

        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {
        mapList.get(visibleMap).render(g, step, scale);
        entityManager.render(visibleMap, step, g);
        int day = step / daySize;
        //int maxWidth = scale*3;
        int startY = scale;
        g.drawString("Day Count : " + day, w + scale/2, startY + 3*(startY/2));

    }

    @Override
    public Object getVar(int id) {
        return mapList.get(id);
    }

    @Override
    public void shutdown(){
        entityManager.shutdown();
        for(LunaMap m : mapList)
            m.shutdown();
    }

    public boolean reset(){
        return entityManager.reset();
    }

    public void resetEntityManager(int newSimId){
        this.simId = newSimId;
        entityManager.shutdown();
        entityManager = new EntityManager(h, w, scale, mapList.size(), turnStep, simId);
        itemManager.shutdown();
        itemManager = new ItemManager(simId);
    }

    public void resetMaps(){
        for (LunaMap lunaMap : mapList) {
            lunaMap.reset();
        }
    }

    public String getReportLine(int step){
        return entityManager.getReportLine(step);
    }

    public void databasePush(){
        entityManager.databasePush();
    }
}
