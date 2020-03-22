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
    private int simId;

    private List<List<Tile>> tileMap;
    private ObjectManager objectManager;

    private int size;
    private int h, w, scale;
    private int food_limit;

    public LunaMap(int HEIGHT, int WIDTH, int world_scale, int simId){
        this.uniqueId = counter;
        this.listId = -1;
        this.tileMap = new ArrayList<>();
        this.size = 5;
        this.simId = simId;
        counter++;
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale, -1, simId);
    }

    public LunaMap(int HEIGHT, int WIDTH, int world_scale, int size, int listId, int simId){
        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;
        this.uniqueId = counter;
        this.listId = listId;
        this.simId = simId;
        this.tileMap = new ArrayList<>();
        this.size = size;
        counter++;
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale, listId, simId);
        // now lets make the maps
        generateMap();

        food_limit = 13; // TODO: keep experimenting to find a good balance for this value
        addFood(food_limit);
    }

    public LunaMap(int HEIGHT, int WIDTH, int world_scale, int listId, List<List<Tile>> tileMap, int simId){
        this.h = HEIGHT;
        this.w = WIDTH;
        this.scale = world_scale;
        this.uniqueId = counter;
        this.listId = listId;
        this.tileMap = tileMap;
        this.size = tileMap.size();
        this.simId = simId;
        counter++;
        objectManager = new ObjectManager(HEIGHT, WIDTH, world_scale, listId, simId);
        // TODO: add creating maps from file input
        FoodBase food = new FoodBase(new int[]{world_scale,world_scale,uniqueId}, 0, world_scale/2);
        boolean result = addWorldObject(food);
        System.out.println("added item? " + result);
    }


    public void update(int step, int turnSize){
        objectManager.update(step, uniqueId);
        if(step % (turnSize * 10) == 0 && step > 0) // need to find a good number for this
            addFood(food_limit);
        //if(step % turnSize == 0 && step > 0)
        //    mapIteration(2, 7, 5);
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

    private void addFood(int amt){
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

    public WorldObject getObject(int y, int x, int idx){
        return objectManager.getObject(y,x,idx);
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

    public void reset(){
        this.objectManager.shutdown();
        this.objectManager = new ObjectManager(h, w, scale, listId, simId);
        addFood(food_limit);
    }

    // TODO: finish the map generator
    //  - needs to be more robust
    //  - needs to make cleaner maps
    private void generateMap(){
        // initial setup
        int[] typeCount = new int[]{0,0,0,0};
        for(int y = 0; y < this.size; y++){
            this.tileMap.add(new ArrayList<>());
            for(int x = 0; x < this.size; x++){

                Tile t = new Tile(new int[]{y, x, this.listId}, 0);
                typeCount[0]++;
                this.tileMap.get(y).add(t);
            }

        }// there

        int chances = (size*size) / 4;
        for(int i = 0; i < chances; i++){
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            int type = 1 + rand.nextInt(3);
            typeCount[type]++;
            this.tileMap.get(y).get(x).setType(type);
        }

        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                System.out.print(this.tileMap.get(y).get(x).getType() + " ");
            }
            System.out.println();
        }
        // TODO: refine map here
    }

    // might remove this
    public void mapIteration(int min, int max, int kernel){
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                // check conditions
                int type = tileMap.get(y).get(x).getType();
                int[] kernelCounts = new int[]{0,0,0,0};
                int similar = 1; // count yourself
                for(int ky = -1; ky < kernel-1; ky++){
                    for(int kx = -1; kx < kernel-1; kx++){
                        if(ky+y >= 0 && kx+x >= 0 && ky+y < this.size && kx+x < this.size){
                            if(ky+y != y && kx+x != x && tileMap.get(ky+y).get(kx+x).getType() == type)
                                similar++;
                            kernelCounts[tileMap.get(ky+y).get(kx+x).getType()]++;
                        }
                    }
                }
                int mode = 1;
                for(int n = 2; n < kernelCounts.length; n++){
                    if(kernelCounts[n] > kernelCounts[mode])
                        mode = n;
                }
                if(similar > max || similar < min)
                    tileMap.get(y).get(x).setType(0);
                else if(kernelCounts[mode] >= min && kernelCounts[mode] <= max)
                    tileMap.get(y).get(x).setType(mode);

            }
        }
    }

    public void shutdown(){
        this.objectManager.shutdown();
    }
}
