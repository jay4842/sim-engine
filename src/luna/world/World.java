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
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                tileMap.get(y).add(new Tile(x,y));
            }
        }

    }//

    public void update() {

        // update entities
        Iterator<Entity> iterator = entities.iterator();
        synchronized (entities){
            while(iterator.hasNext()){
                iterator.next().update(tileMap);
            }
        }
    }

    //
    public void render(Graphics2D g) {
        // render entities
        Iterator<Entity> iterator = entities.iterator();
        synchronized (entities){
            while(iterator.hasNext()){
                iterator.next().render(g);
            }
        }

    }//
}
