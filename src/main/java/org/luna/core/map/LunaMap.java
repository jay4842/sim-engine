package org.luna.core.map;

import org.luna.core.object.WorldObject;
import org.luna.core.object.food.FoodBase;
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

    public LunaMap(int HEIGHT, int WIDTH, int world_scale){
        this.uniqueId = counter;
        this.listId = -1;
        this.tileMap = new ArrayList<>();
        this.size = 5;
        counter++;
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale);
    }

    public LunaMap(int HEIGHT, int WIDTH, int world_scale, int size, int listId){
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

        int food_amt = 25;
        for(int i = 0; i < food_amt; i++) {
            int x = rand.nextInt(WIDTH-world_scale);
            int y = rand.nextInt(HEIGHT-world_scale);
            FoodBase food = new FoodBase(new int[]{y, x, uniqueId}, i, world_scale / 2);
            boolean result = addWorldObject(food);
            System.out.println("added item? " + result);
        }
    }

    public LunaMap(int HEIGHT, int WIDTH, int world_scale, int listId, List<List<Tile>> tileMap){
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


    public void update(int step){
        objectManager.update(step, uniqueId);
    }

    public void render(Graphics2D g, int tileScale){
        for(int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                tileMap.get(y).get(x).render(g, tileScale);
            }
        }
        objectManager.render(uniqueId, g);

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

    public List<WorldObject> getObjectsInMap() {
        return objectManager.getObjectList();
    }

    public int getSize() {
        return size;
    }

    private boolean addWorldObject(WorldObject o){
        if(!objectManager.getObjectList().contains(o)){
            objectManager.add(o);
            return true;
        }
        return false;
    }


}
