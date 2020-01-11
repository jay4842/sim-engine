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


    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        mapList.get(visibleMap).update(step, turnStep);
        entityManager.update(step, visibleMap, mapList.get(x));
        // TODO
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

    public String getReportLine(){
        return entityManager.getReportLine();
    }

    public void databasePush(){
        entityManager.databasePush();
    }
}
