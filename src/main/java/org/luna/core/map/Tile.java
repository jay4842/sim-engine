package org.luna.core.map;

import org.luna.core.util.ImageUtility;

import java.awt.*;
import java.awt.image.BufferedImage;

// no updates, just a data structure
public class Tile {
    private static final int NUM_TILES = 4;
    private static ImageUtility imageUtil = new ImageUtility();
    private static int counter = 0;
    private int uniqueId;
    private int[] gps; // gps is used to reference the location in the list as well
    private int type;
    private BufferedImage tileImage;

    // TODO: add objects in tile list

    public Tile(){
        this.uniqueId = counter;
        this.gps = new int[3];
        this.type = 0;
        tileImage = ImageUtility.load("res/tile/grass_tile.png");
        counter++;
    }

    public Tile(int[] gps, int type){
        this.uniqueId = counter;
        this.gps = gps;
        this.type = type;
        counter++;
    }

    public void render(Graphics2D g, int scale){
        // TODO, add tile images based on tile type
        g.drawImage(imageUtil.getTileImage(getTileName()), gps[0]*scale, gps[1]*scale, scale, scale, null);
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public int[] getGps() {
        return gps;
    }

    public int getType() {
        return type;
    }

    public static int getCounter() {
        return counter;
    }

    public String getTileName(){
        switch (type){
            case 0: return "grass";
            case 1: return "barren";
            case 2: return "dune";
            case 3: return "mountain";
            default: return "none";
        }
    }
}
