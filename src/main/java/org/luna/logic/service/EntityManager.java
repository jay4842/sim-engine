package org.luna.logic.service;

import org.luna.core.entity.Entity;
import org.luna.core.map.LunaMap;
import org.luna.core.map.Tile;
import org.luna.core.util.ManagerCmd;
import org.luna.core.util.Utility;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

// Hold and manager all the entities in the game
// - note: entities will be
public class EntityManager implements Manager {

    private static Utility utility = new Utility();
    private static List<Entity> entities;
    private static List<List<Integer[]>> entityRef;
    private static List<Integer> sizesPerStep;

    private int h, w, s;

    EntityManager(int HEIGHT, int WIDTH, int world_scale, int numMaps){
        this.h = HEIGHT;
        this.w = WIDTH;
        this.s = world_scale;
        entities = new ArrayList<>();
        entityRef = new ArrayList<>();
        sizesPerStep = new ArrayList<>();
        for(int i = 0; i < numMaps; i++)
            entityRef.add(new ArrayList<>());
        // spawn some entities
        int spawns = 100;
        for(int i = 0; i < spawns; i++){
            int x = Utility.getRnd().nextInt(WIDTH-world_scale);
            int y = Utility.getRnd().nextInt(HEIGHT-world_scale);
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

        int alive = entities.size();
        sizesPerStep.add(alive);
        return null;
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        int alive = entities.size();
        sizesPerStep.add(alive);
        return null;
    }

    @Override
    public void render(int visibleMap, Graphics2D g) {

        // go through the list and render the entity if it is in the visible map
        for(Entity e : entities){
            if(e.getGps()[2] == visibleMap)
                e.render(g);
        }

        // draw stats to the right
        g.setColor(Color.black);
        g.setFont(Utility.getSmallFont());
        g.drawString("Entities: " + entities.size(), w + s, s);
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }
}
