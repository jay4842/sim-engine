package luna.world;

import luna.util.Util;
import luna.entity.Entity;
import luna.util.Tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// This guy will just hold all of our entities

public class World {
    Util util = new Util();
    /*
     * Still working out how I want the map to be handled
     * */
    public static List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());

    //
    int width, height, world_scale;
    int entityCount = 0;
    public static List<List<Tile>> tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
    Color gridColor = new Color(0,0,0, 30);
    public World(int width, int height, int world_scale) {
        this.width = width;
        this.height = height;
        this.world_scale = world_scale;
        // entity initial setups
        for (int i = 0; i < 1; i++) {
            int x = (int) (Math.random() * 200) + 10;
            int y = (int) (Math.random() * 200) + 10;
            Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            entities.add(new Entity(x, y, this.width, this.height, world_scale, c, entityCount));
            entityCount += 1;
        }
        // tile map initial setup
        util.println("TileMap size: [" + Math.floor(height/world_scale) + "]" + "[" + Math.floor(width/world_scale) + "]");
        int count = 0;
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count,this.world_scale,this.height,this.width, 0));
                count++;
            }
        }

    }//

    public void update(int seconds) {

        // update entities
        Iterator<Entity> iterator = entities.iterator();
        synchronized (entities){
            while(iterator.hasNext()){
                iterator.next().update(tileMap, seconds);
            }
        }
        // update all tiles
        Iterator<List<Tile>> tileIterator = tileMap.iterator();
        synchronized (tileIterator){
            while(tileIterator.hasNext()){
                List<Tile> subTile = tileIterator.next();
                for (int i = 0; i < subTile.size(); i++) {
                    subTile.get(i).update(seconds);
                }

            }
        }// end of tile updater

    }

    //
    public void render(Graphics2D g) {
        //
        g.setColor(gridColor);
        for(int y = 0; y < height/world_scale; y++) {
            for (int x = 0; x < width / world_scale; x++) {
                g.drawRect(x*world_scale,y*world_scale,world_scale,world_scale);
            }
        }
        //
        Iterator<List<Tile>> tileIterator = tileMap.iterator();
        synchronized (tileIterator){
            while(tileIterator.hasNext()){
                List<Tile> subTile = tileIterator.next();
                for (int i = 0; i < subTile.size(); i++) {
                    subTile.get(i).render(g);
                }

            }
        }// end of tile updater
        // render entities
        Iterator<Entity> iterator = entities.iterator();
        synchronized (entities){
            while(iterator.hasNext()){
                iterator.next().render(g);
            }
        }
        //render tiles


    }//
}
