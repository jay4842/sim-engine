package org.luna.core.map;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Map {
    private static int counter = 0;
    private int listId;
    private int uniqueId;
    private List<List<Tile>> tileMap;
    private int size;

    public Map(){
        this.uniqueId = counter;
        this.listId = -1;
        this.tileMap = new ArrayList<>();
        this.size = 5;
        counter++;
    }

    public Map(int size, int listId){
        this.uniqueId = counter;
        this.listId = listId;
        this.tileMap = new ArrayList<>();
        this.size = size;
        counter++;
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
    }

    public Map(int listId, List<List<Tile>> tileMap){
        this.uniqueId = counter;
        this.listId = listId;
        this.tileMap = tileMap;
        this.size = tileMap.size();
        counter++;
        // TODO: add creating maps from file input

    }

    public void render(Graphics2D g, int tileScale){
        for(int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                tileMap.get(y).get(x).render(g, tileScale);
            }
        }

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
}
