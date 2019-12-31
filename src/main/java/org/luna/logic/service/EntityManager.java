package org.luna.logic.service;

import org.luna.core.entity.Entity;
import org.luna.core.entity.variants.MutationA;
import org.luna.core.map.LunaMap;
import org.luna.core.map.Tile;
import org.luna.core.util.ManagerCmd;
import org.luna.core.util.Utility;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Hold and manager all the entities in the game
// - note: entities will be
public class EntityManager implements Manager {

    private static Utility utility = new Utility();
    public static List<Entity> entities;
    public static List<List<Integer[]>> entityRef;
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
        int spawns = 20;
        for(int i = 0; i < spawns; i++){
            Entity e = makeEntity(0);
            entities.add(e);
        }

    }

    private static final int turnSize = 10;
    public List<ManagerCmd> update(int step, int visibleMap, LunaMap map) {
        List<Entity> addBuffer = new ArrayList<>();
        for(int i = 0; i < entities.size(); i++){
            entities.get(i).update(step, map);

            if(entities.get(i).replicate() && step % turnSize == 0 && step > 0){
                addBuffer.add(entities.get(i).makeEntity());
            }else if(entities.get(i).isDead() && step % turnSize == 0 && step > 0){
                entities.get(i).deleteSelfFromRef();
                entities.remove(i);
                i--;
            }
        }

        entities.addAll(addBuffer);
        if(step % turnSize == 0) {
            int alive = entities.size();
            sizesPerStep.add(alive);
        }
        return null;
    }

    private Entity makeEntity(int map){
        int x = Utility.getRnd().nextInt(w-s);
        int y = Utility.getRnd().nextInt(h-s);
        return new MutationA(s, new int[]{y,x,map});
    }


    @Override
    public List<ManagerCmd> update(int step, int x) {
        int alive = entities.size();
        sizesPerStep.add(alive);
        return null;
    }

    @Override
    public void render(int visibleMap, int step, Graphics2D g) {

        // go through the list and render the entity if it is in the visible map
        for(Entity e : entities){
            if(e.getGps()[2] == visibleMap)
                e.render(g);
        }

        // draw stats to the right
        g.setColor(Color.black);
        g.setFont(Utility.getSmallFont());
        g.drawString("Step     : " + step, w + s, s);
        g.drawString("Entities : " + entities.size(), w + s, s*2);
        g.drawString("avg      : " + String.format("%.2f", Utility.getAverage(sizesPerStep.toArray()) ), w + s, s*3);
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){
        // todo:
        //  log all data from this run
    }

    public boolean reset(){
        return entities.size() <= 0;
    }
}
