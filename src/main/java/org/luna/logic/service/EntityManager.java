package org.luna.logic.service;

import org.luna.core.entity.Entity;
import org.luna.core.map.Tile;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

// Hold and manager all the entities in the game
// - note: entities will be
public class EntityManager implements Manager {

    private List<Entity> entities;

    public EntityManager(int world_scale){
        entities = new ArrayList<>();

        Entity e = new Entity(world_scale, new int[]{100,100,0});
        entities.add(e);
    }

    public List<ManagerCmd> update(int step, int visibleMap, List<List<Tile>> tileMap) {
        for(Entity e : entities){
            if(e.getGps()[2] == visibleMap)
                e.update(step, tileMap);
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
