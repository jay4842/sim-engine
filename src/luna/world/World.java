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
    // This list will consist of every entity in the world
    // - entities in sub maps will be storied here but will use the position indicator to mark where they are rendered.
    public static List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());

    //
    int width;
    int height;
    static int world_scale;
    int entityCount = 0;
    boolean initialized = false;
    public static List<List<Tile>> tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
    public static List<Map> subMaps = Collections.synchronizedList(new ArrayList<>());

    public static int visibleMap = -1; // debug item for viewing a map that an entity visits

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
                //tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count,this.world_scale,this.height,this.width, 0));
                if(y == 5 && x == 5)
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count,this.world_scale,this.height,this.width, 2));
                else if (Math.random()*100 > 95)
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count,this.world_scale,this.height,this.width, 1));
                else
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count,this.world_scale,this.height,this.width, -1));
                //*/
                count++;
            }
        }

    }//

    // there needs to be an initial entity update
    public void update(int seconds) {
        // TODO: When more sub tiles are added, then I can add updating all the tiles later
        // update entities
        if(!initialized){
            Iterator<Entity> itr = entities.iterator();
            List<Entity> dead = new ArrayList<>();
            synchronized (entities){
                while(itr.hasNext()){
                    Entity tmp = itr.next();
                    if(tmp.getPosition() > -1){
                        if(tmp.isAlive())tmp.update(subMaps.get(tmp.getPosition()).getTileMap(), seconds);
                    }else
                    if(tmp.isAlive())tmp.update(tileMap, seconds);
                    //
                }
            }// //
            initialized = true;
        }

        Iterator<Map> mapIterator = subMaps.iterator();
        synchronized (mapIterator){
            while(mapIterator.hasNext()){
                mapIterator.next().update();
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

        Iterator<Entity> iterator = entities.iterator();
        List<Entity> dead = new ArrayList<>();
        synchronized (entities){
            while(iterator.hasNext()){
                Entity tmp = iterator.next();
                if(tmp.getPosition() > -1){
                    if(tmp.isAlive())tmp.update(subMaps.get(tmp.getPosition()).getTileMap(), seconds);
                }else
                    if(tmp.isAlive())tmp.update(tileMap, seconds);
                //
            }
        }// //

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
            boolean mapRendered = false;
            while(iterator.hasNext()){
                Entity tmp = iterator.next();
                if(tmp.getPosition() > -1 && visibleMap > -1){
                    if(!mapRendered){
                        subMaps.get(visibleMap).render(g);
                        mapRendered = true;
                    }
                    if(tmp.getPosition() == visibleMap && tmp.isAlive())tmp.render(g);
                }else if(tmp.getPosition() == -1)
                    if(tmp.isAlive()) tmp.render(g);
            }
        }
        //render tiles


    }//

    // adding new maps
    public static void addMap(List<List<Tile>> tileMap){
        int pos = 0;
        if(subMaps.size() > 0) pos = subMaps.size()-1;
        Map m = new Map(pos, tileMap, world_scale);
        subMaps.add(m);
    }

    public static void addMap(List<List<Tile>> tileMap, int objectID){
        int pos = 0;
        if(subMaps.size() > 0) pos = subMaps.size()-1;
        Map m = new Map(pos, tileMap, world_scale);
        m.setObjectID(objectID);
        subMaps.add(m);
    }

    // removing a map
    public static void removeMap(int i){
        if(i < 0 || i > subMaps.size()-1){
            System.out.println("Error: " + i + " is not a valid map position");
            return;
        }
        subMaps.remove(i);
    }//

    //
    public static Map getMap(int i){
        if(i < 0 || i > subMaps.size()-1){
            System.out.println("Error: " + i + " is not a valid map position");
            return null;
        }
        return subMaps.get(i);
    }//

    // Return size - 1 for positioning
    public static int getMapListSize(){
        return subMaps.size();
    }

    // close logs, save will be done here eventually.
    public void shutdown(){
        // shutdown each entity, currently just closes the logs
        Iterator<Entity> iterator = entities.iterator();
        synchronized (entities){
            while(iterator.hasNext()){
                iterator.next().shutdown();
            }
        }
        // shutdown maps
        for(Map sub : subMaps){
            sub.shutdown();
        }
    }
}
