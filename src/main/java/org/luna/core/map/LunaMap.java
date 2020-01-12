package org.luna.core.map;

import org.luna.core.object.WorldObject;
import org.luna.core.object.food.FoodBase;
import org.luna.logic.service.Manager;
import org.luna.logic.service.ObjectManager;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class LunaMap {
    private static SecureRandom rand = new SecureRandom();
    private static int counter = 0;
    private int listId;
    private int uniqueId;
    private List<List<Tile>> tileMap;
    private ObjectManager objectManager;

    private int size;
    private int h, w, scale;
    private int food_limit;

    public LunaMap(int HEIGHT, int WIDTH, int world_scale){
        this.uniqueId = counter;
        this.listId = -1;
        this.tileMap = new ArrayList<>();
        this.size = 5;
        counter++;
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale);
    }

    public LunaMap(int HEIGHT, int WIDTH, int world_scale, int size, int listId){
        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;
        this.uniqueId = counter;
        this.listId = listId;
        this.tileMap = new ArrayList<>();
        this.size = size;
        counter++;
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale);
        // now lets make the maps
        for(int y = 0; y < this.size; y++){
            this.tileMap.add(new ArrayList<>());
            for(int x = 0; x < this.size; x++){
                System.out.print("_ ");
                Tile t = new Tile(new int[]{y, x, this.listId}, 0);
                this.tileMap.get(y).add(t);
            }
            System.out.println();
        }// there

        food_limit = 10;
        addFood(food_limit);
    }

    public LunaMap(int HEIGHT, int WIDTH, int world_scale, int listId, List<List<Tile>> tileMap){
        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;
        this.uniqueId = counter;
        this.listId = listId;
        this.tileMap = tileMap;
        this.size = tileMap.size();
        counter++;
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale);
        // TODO: add creating maps from file input
        FoodBase food = new FoodBase(new int[]{world_scale,world_scale,uniqueId}, 0, world_scale/2);
        boolean result = addWorldObject(food);
        System.out.println("added item? " + result);
    }


    public void update(int step, int turnSize){
        objectManager.update(step, uniqueId);
        if(step % (turnSize * 10) == 0 && step > 0) // need to find a good number for this
            addFood(food_limit);
    }

    public void render(Graphics2D g, int step, int tileScale){
        for(int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                tileMap.get(y).get(x).render(g, tileScale);
            }
        }
        objectManager.render(uniqueId, step, g);

    }

    public static int getCounter() {
        return counter;
    }

    public int getListId() {
        return listId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public List<List<Tile>> getTileMap() {
        return tileMap;
    }

    public List<List<List<WorldObject>>> getObjectsInMap() {
        return objectManager.getObjectMap();
    }

    public int getSize() {
        return size;
    }

    private boolean addWorldObject(WorldObject o){
        return objectManager.add(o);
    }

    public void addFood(int amt){
        for(int i = 0; i < amt; i++) {
            int x = rand.nextInt(w-scale);
            int y = rand.nextInt(h-scale);
            FoodBase food = new FoodBase(new int[]{y, x, uniqueId}, i, scale / 2);
            boolean result = addWorldObject(food);
            System.out.println("result? " + result);
        }

        /*for(int y = 0; y < h/scale; y++) {
            for (int x = 0; x < w / scale; x++) {
                if(getObjectsInMap().get(y).get(x).size() > 0)
                    System.out.print(getObjectsInMap().get(y).get(x).size() + " ");
                else
                    System.out.print("_ ");
            }
            System.out.println();
        }*/

    }

    public boolean removeObject(int y, int x, int idx){
        return objectManager.removeObject(y,x,idx);
    }

    public boolean isObjectInMap(int y, int x, int idx){
        return objectManager.isObjectInMap(y,x,idx);
    }

    public ObjectManager getObjectManager(){return this.objectManager;}

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    public int getScale() {
        return scale;
    }
}
