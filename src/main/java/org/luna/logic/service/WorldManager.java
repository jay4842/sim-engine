package org.luna.logic.service;

import org.luna.core.map.LunaMap;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

// Hold all of the maps in the world
public class WorldManager implements Manager{

    // sense objects belong to the world, it will belong to the world manager as well
    private EntityManager entityManager;

    private List<LunaMap> mapList;
    private int visibleMap = 0;
    private int h, w, scale;

    public WorldManager(int HEIGHT, int WIDTH, int world_scale){
        System.out.println("Making world manager");
        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;

        int size = HEIGHT/world_scale;
        // lets make some maps
        mapList = new ArrayList<>();

        LunaMap overWorld = new LunaMap(HEIGHT, WIDTH, world_scale, size, 0);
        mapList.add(overWorld);

        // now populate other managers
        entityManager = new EntityManager(HEIGHT, WIDTH, world_scale, mapList.size());


    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        mapList.get(visibleMap).update(step);
        entityManager.update(step, visibleMap, mapList.get(x));
        // TODO
        return null;
    }

    @Override
    public void render(int x, Graphics2D g) {
        mapList.get(visibleMap).render(g, scale);
        entityManager.render(visibleMap, g);

    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }


}
