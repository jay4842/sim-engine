package org.luna.logic.service;

import org.luna.core.object.WorldObject;
import org.luna.core.object.food.FoodBase;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

// Handle all world objects
public class ObjectManager implements Manager{

    private List<List<List<WorldObject>>> objectMap;
    private int h, w, scale;

    public ObjectManager(int HEIGHT, int WIDTH, int world_scale){
        this.w = WIDTH;
        this.h = HEIGHT;
        this.scale = world_scale;

        objectMap = new ArrayList<>();
        for(int y = 0; y < HEIGHT/world_scale; y++){
            objectMap.add(new ArrayList<>());
            for(int x = 0; x < WIDTH/world_scale; x++){
                objectMap.get(y).add(new ArrayList<>());
            }
        }// done creating object map
    }

    @Override
    public List<ManagerCmd> update(int step, int n) {
        for(int y = 0; y < h/scale; y++){
            for(int x = 0; x < w/scale; x++) {
                for (WorldObject obj : objectMap.get(y).get(x)) {
                    if (obj.getGps()[2] == n)
                        obj.update(step, n);
                }
            }
        }
        return null;
    }

    @Override
    public void render(int n, int step, Graphics2D g) {
        for(int y = 0; y < h/scale; y++){
            for(int x = 0; x < w/scale; x++) {
                for (WorldObject obj : objectMap.get(y).get(x)) {
                    if (obj.getGps()[2] == n)
                        obj.render(n, g);
                }
            }
        }
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }

    public List<List<List<WorldObject>>> getObjectMap() {
        return objectMap;
    }

    // adds item to the object map
    public boolean add(WorldObject obj){
        int y = obj.getGps()[0]/scale;
        int x = obj.getGps()[1]/scale;
        int listId = 0;
        if(objectMap.get(y).get(x).size() > 0)
            listId = objectMap.get(y).get(x).size();
        obj.setListId(listId);
        return this.objectMap.get(y).get(x).add(obj);
    }

    public boolean reset(){
        return false;
    }

    public String getReportLine(){
        return ""; // will report on object in map eventually
    }

    public void databasePush(){
        // TODO
    }
}
