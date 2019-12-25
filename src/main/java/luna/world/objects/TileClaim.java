package luna.world.objects;

import luna.main.Game;
import luna.util.Tile;
import luna.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

// Tile claims create sub maps
// - they prevent permanent encounters
// - created by Groups, and Communities
public class TileClaim extends ObjectOfInterest{

    private int size;

    public TileClaim(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        int subMapSize = (Util.random(4) + 1) + 4;
        createMap(subMapSize);
    }

    public TileClaim(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale, boolean test) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        int subMapSize = (Util.random(4) + 1) + 4;
        if(test)
            subMapSize = 4;
        createMap(subMapSize);
    }

    public void createMap(int size){
        this.size = size;
        tileMap = Collections.synchronizedList(new ArrayList<>());
        int count = 0;
        for(int y = 0; y < size; y++) {
            tileMap.add(new ArrayList<>());
            for (int x = 0; x < size; x++) {
                tileMap.get(y).add(new Tile(x* Game.sub_world_scale, y*Game.sub_world_scale,count,Game.sub_world_scale,world_h,world_w,-1, this.tileMapPos));
                count++;
            }
        }
    }

    public int getSize() {
        return size;
    }

    public void update(int seconds){
        super.update(seconds);
    }

    public void render(Graphics2D g){
        //System.out.println("rendering tile claim");
        super.render(g);
        g.setColor(Color.green);
        g.fillRect(getxPos(), getyPos(), getWorld_scale(), getWorld_scale());
        g.setColor(Color.black);
        //g.drawString("" + isActive() + " " + getTileMapPos(), xPos, yPos+(getWorld_scale()-5));
    }

    public String toString(){
        return "ID: " + getObjectID() + " GPS: " + getCurrTileY() + " " + getCurrTileX() + " " + getTileMapPos();
    }
}
