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

    public WorldManager(int HEIGHT, int WIDTH, int world_scale, int turnStep){
        System.out.println("Making world manager");
        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;
        this.turnStep = turnStep;

        int size = HEIGHT/world_scale;
        // lets make some maps
        mapList = new ArrayList<>();

        LunaMap overWorld = new LunaMap(HEIGHT, WIDTH, world_scale, size, 0);
        mapList.add(overWorld);

        // now populate other managers
        entityManager = new EntityManager(HEIGHT, WIDTH, world_scale, mapList.size(), turnStep);
        itemManager = new ItemManager();
        itemManager.createItemRefs();
    }

    @Override
    public List<ManagerCmd> update(int step, int n) {
        mapList.get(visibleMap).update(step, turnStep);
        List<ManagerCmd> cmds = entityManager.update(step, visibleMap, mapList.get(n));
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
                    System.out.println("From worldManager: call(removeObject[" + y + "," + x + "," + idx + "]): " + mapList.get(n).removeObject(y,x,idx));
                    if(obj.getItemRefId() != -1){
                        Item item = itemManager.createItem(obj.getItemRefId());
                        item.setListID(itemManager.items.size());
                        itemManager.items.add(item);
                        entityManager.addItemToEntity(entity_idx, item);
                    }
                }

            }
        }
        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {
        mapList.get(visibleMap).render(g, step, scale);
        entityManager.render(visibleMap, step, g);

    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){
        entityManager.shutdown();
    }

    public boolean reset(){
        return entityManager.reset();
    }

    public void resetEntityManager(){
        entityManager.shutdown();
        entityManager = new EntityManager(h, w, scale, mapList.size(), turnStep);
    }

    public void resetMaps(){
        for(int i = 0; i < mapList.size(); i++){
            mapList.get(i).reset();
        }
    }

    public String getReportLine(){
        return entityManager.getReportLine();
    }

    public void databasePush(){
        entityManager.databasePush();
    }
}
