package org.luna.logic.service;

import org.luna.core.object.WorldObject;
import org.luna.core.object.food.FoodBase;
import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

// Handle all world objects
public class ObjectManager implements Manager{

    private List<WorldObject> objectList;

    public ObjectManager(int HEIGHT, int WIDTH, int world_scale){
        objectList = new ArrayList<>();
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {

        for(WorldObject obj : objectList){
            if(obj.getGps()[2] == x)
                obj.update(step, x);
        }
        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {
        for(WorldObject obj : objectList){
            if(obj.getGps()[2] == x)
                obj.render(x, g);
        }
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }

    public List<WorldObject> getObjectList() {
        return objectList;
    }

    public void add(WorldObject obj){
        objectList.add(obj);
    }

    public boolean reset(){
        return false;
    }

}
