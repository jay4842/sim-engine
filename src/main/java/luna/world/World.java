package luna.world;

import luna.entity.util.EntityManager;
import luna.util.Util;
import luna.entity.Entity;
import luna.util.Tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

// This guy will just hold all of our entities

// TODO: Working on more sub map mechanics
//  [_] home/base maps
//  [_] other types of sub maps
//  [_] Adding tile sprites
//  [_] fights between main entities
//  [_] supporting groups (will add task to entity)
public class World {
    Util util = new Util();
    // This list will consist of every entity in the world
    // - entities in sub maps will be storied here but will use the position indicator to mark where they are rendered.
    public static EntityManager entityManager;
    int width;
    int height;
    static int world_scale;
    int entityCount = 0;
    int spawnLimit = 15;
    boolean initialized = false;
    public static List<List<Tile>> tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
    public static List<Map> subMaps = Collections.synchronizedList(new ArrayList<>());

    public static int visibleMap = -1; // debug item for viewing a map that an entity visits

    Color gridColor = new Color(0,0,0, 30);
    public World(int width, int height, int world_scale) {
        this.width = width;
        this.height = height;
        this.world_scale = world_scale;
        this.entityManager = new EntityManager();

        // entity initial setups
        for (int i = 0; i < spawnLimit; i++) {
            int x = (int) (Math.random() * 200) + 10;
            int y = (int) (Math.random() * 200) + 10;
            //x = 32*5;
            //y = 32*5;
            Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            entityManager.entities.add(new Entity(x, y, this.width, this.height, world_scale, c, entityCount));
            entityCount += 1;
        }
        // tile map initial setup
        util.println("TileMap size: [" + Math.floor(height/world_scale) + "]" + "[" + Math.floor(width/world_scale) + "]");
        int count = 0;
        int food = 0;
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count,this.world_scale,this.height,this.width, 0));
                /*if(y == 5 && x == 5)
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count,this.world_scale,this.height,this.width, 2));
                else if (Math.random()*100 > 95 || count==0) {
                    tileMap.get(y).add(new Tile(x * world_scale, y * world_scale, count, this.world_scale, this.height, this.width, 1));
                    count++;
                }
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
            Iterator<Entity> itr = entityManager.entities.iterator();
            List<Entity> dead = new ArrayList<>();
            synchronized (entityManager.entities){
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

        Iterator<Entity> iterator =entityManager.entities.iterator();
        List<Entity> dead = new ArrayList<>();
        synchronized (entityManager.entities){
            while(iterator.hasNext()){
                Entity tmp = iterator.next();
                if(tmp != null) {
                    if (tmp.getPosition() > -1) {
                        if (tmp.isAlive()) tmp.update(subMaps.get(tmp.getPosition()).getTileMap(), seconds);
                    } else if (tmp.isAlive()) tmp.update(tileMap, seconds);
                }
                //
            }
        }// //

        entityManager.update(seconds);

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
        Iterator<Entity> iterator = entityManager.entities.iterator();
        synchronized (entityManager.entities){
            boolean mapRendered = false;
            while(iterator.hasNext()){
                Entity tmp = iterator.next();
                if(tmp != null){
                    if(tmp.getPosition() > -1 && visibleMap > -1){
                        if(!mapRendered){
                            subMaps.get(visibleMap).render(g);
                            mapRendered = true;
                        }
                        if(tmp.getPosition() == visibleMap && tmp.isAlive())tmp.render(g);
                    }else if(tmp.getPosition() == -1)
                        if(tmp.isAlive()) tmp.render(g);
                    if(!tmp.isAlive()){
                        tmp.shutdown();
                        tmp = null;
                    }
                }
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
        Iterator<Entity> iterator = entityManager.entities.iterator();
        synchronized (entityManager.entities){
            while(iterator.hasNext()){
                Entity tmp = iterator.next();
                if(tmp != null){
                    tmp.shutdown();
                }
            }
        }
        // shutdown maps
        for(Map sub : subMaps){
            sub.shutdown();
        }
    }

    // for adding, removing from the entity ref map
    public static void editRefMap(String cmd, int pos, int id){
        entityManager.editRefMap(cmd,pos,id);
    }
}
