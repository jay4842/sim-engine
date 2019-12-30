package org.luna.logic.service;

import org.luna.core.map.Map;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

// Hold all of the maps in the world
public class WorldManager implements Manager{

    // sense objects belong to the world, it will belong to the world manager as well
    private ObjectManager objectManager;
    private EntityManager entityManager;

    private List<Map> mapList;
    private int visibleMap = 0;
    private int h, w, scale;

    public WorldManager(int HEIGHT, int WIDTH, int world_scale){
        System.out.println("Making world manager");
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale);
        entityManager = new EntityManager(world_scale);

        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;

        int size = HEIGHT/world_scale;
        // lets make some maps
        mapList = new ArrayList<>();

        Map overWorld = new Map(size, 0);
        mapList.add(overWorld);
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        entityManager.update(step, visibleMap, mapList.get(x).getTileMap());
        objectManager.update(step, visibleMap);
        // TODO
        return null;
    }

    @Override
    public void render(int x, Graphics2D g) {
        mapList.get(visibleMap).render(g, scale);
        entityManager.render(visibleMap, g);
        objectManager.render(visibleMap, g);

    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }


}
