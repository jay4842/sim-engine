package org.luna.logic.service;

import org.luna.core.entity.Entity;
import org.luna.core.map.LunaMap;
import org.luna.core.map.Tile;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

// Hold and manager all the entities in the game
// - note: entities will be
public class EntityManager implements Manager {

    private static SecureRandom rand = new SecureRandom();
    private static List<Entity> entities;
    private static List<List<Integer[]>> entityRef;

    EntityManager(int HEIGHT, int WIDTH, int world_scale, int numMaps){
        entities = new ArrayList<>();
        entityRef = new ArrayList<>();
        for(int i = 0; i < numMaps; i++)
            entityRef.add(new ArrayList<>());
        // spawn some entities
        int spawns = 100;
        for(int i = 0; i < spawns; i++){
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            Entity e = new Entity(world_scale, new int[]{y,x,0});
            entities.add(e);
        }

    }

    public List<ManagerCmd> update(int step, int visibleMap, LunaMap map) {
        for(Entity e : entities){
            if(e.getGps()[2] == visibleMap){
                e.update(step, map, entities, entityRef);

            }
        }
        return null;
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        return null;
    }

    @Override
    public void render(int visibleMap, Graphics2D g) {

        // go through the list and render the entity if it is in the visible map
        for(Entity e : entities){
            if(e.getGps()[2] == visibleMap)
                e.render(g);
        }
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }
}
