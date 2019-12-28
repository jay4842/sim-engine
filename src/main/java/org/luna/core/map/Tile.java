package org.luna.core.map;

// no updates, just a data structure
public class Tile {
    static int counter = 0;
    int id;
    int[] gps;
    int type;
    // TODO: add objects in tile list

    public Tile(){
        this.id = counter;
        this.gps = new int[3];
        this.type = 0;
        counter++;
    }

    public Tile(int[] gps, int type){
        this.id = counter;
        this.gps = gps;
        this.type = type;
        counter++;
    }
}
