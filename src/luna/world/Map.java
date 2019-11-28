package luna.world;

import luna.entity.Entity;
import luna.main.Game;
import luna.util.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// This class will hold the 2D tile arrays and also manage the entities within them.
//  It should be the only way to correctly and efficiently manage sub maps
public class Map {

    protected List<List<Tile>> tileMap;
    protected List<Entity> entities;
    int overwordX, overworldY, width, height, world_scale;
    int mapPos = -1;
    Color shadow = new Color(0,0,0,80);

    // Constructor where the tile map is made using the default 5x5 size
    public Map(int mapPos, int world_scale){
        this.world_scale = world_scale;
        width = 5;
        height = 5;
        this.mapPos = mapPos;
        int count = 0;
        tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                this.tileMap.get(y).add(new Tile(x*world_scale, y*world_scale,count,
                        world_scale,height*world_scale,width*world_scale,-1));
                count++;
            }
        }// end of making the map
    }

    // constructor with size parameter
    public Map(int mapPos,int mapSize, int world_scale){
        this.world_scale = world_scale;
        width = mapSize;
        height = mapSize;
        this.mapPos = mapPos;
        int count = 0;
        tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                this.tileMap.get(y).add(new Tile(x*world_scale, y*world_scale,count,
                        world_scale,height*world_scale,width*world_scale,-1));
                count++;
            }
        }// end of making the map
    }

    // constructor where the tilemap is passed
    public Map(int mapPos, List<List<Tile>> tileMap, int world_scale){
        this.tileMap = tileMap;
        this.mapPos = mapPos;
        this.world_scale = world_scale;
        this.height = tileMap.size();
        this.width = tileMap.size();
    }//

    // render
    public void render(Graphics2D g){
        // debug function for now, will be called later when I implement an observer roll

        g.setColor(shadow);
        g.fillRect(Game.ACTUAL_WIDTH-(width*world_scale)-10,10,width*world_scale, height*world_scale);

        Iterator<List<Tile>> tileIterator = tileMap.iterator();
        synchronized (tileIterator){
            while(tileIterator.hasNext()){
                List<Tile> subTile = tileIterator.next();
                for (int i = 0; i < subTile.size(); i++) {
                    subTile.get(i).render(g, getRenderXStart(), getRenderYStart());
                }

            }
        }// end of tile updater
    }

    // update
    public void update(){
        // update
    }

    public List<List<Tile>> getTileMap() {
        return tileMap;
    }

    public void setTileMap(List<List<Tile>> tileMap) {
        this.tileMap = tileMap;
    }

    public int getOverwordX() {
        return overwordX;
    }

    public void setOverwordX(int overwordX) {
        this.overwordX = overwordX;
    }

    public int getOverworldY() {
        return overworldY;
    }

    public void setOverworldY(int overworldY) {
        this.overworldY = overworldY;
    }

    public int getWorld_scale() {
        return world_scale;
    }

    public void setWorld_scale(int world_scale) {
        this.world_scale = world_scale;
    }

    public int getMapPos() {
        return mapPos;
    }

    public void setMapPos(int mapPos) {
        this.mapPos = mapPos;
    }

    public int getWidth(){
        return this.width*this.world_scale;
    }

    public int getHeight(){
        return this.height*this.world_scale;
    }

    // for rendering entites, I don't have a way of calculating this on their side, lets just do it here.
    public int getRenderXStart(){return Game.ACTUAL_WIDTH-(width*world_scale)-10;}
    public int getRenderYStart(){return 10;}

}
