package luna.world;

import luna.entity.util.EntityManager;
import luna.main.Game;
import luna.util.Logger;
import luna.util.Manager;
import luna.util.Util;
import luna.entity.Entity;
import luna.util.Tile;
import luna.world.util.ObjectManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

// This guy will just hold all of our entities

// TODO: Working on more sub map mechanics
//  [_] home/base maps
//  [_] other types of sub maps
//  [_] Adding tile sprites
//  [_] fights between main entities
public class World {
    Util util = new Util();
    // This list will consist of every entity in the world
    // - entities in sub maps will be storied here but will use the position indicator to mark where they are rendered.
    private int width;
    private int height;

    private static Manager manager;
    static private int world_scale;
    private boolean initialized = false;
    private int offloadTimer = 0;
    public static List<List<Tile>> tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
    public static List<Map> subMaps = Collections.synchronizedList(new ArrayList<>());
    private Logger worldLogger;

    public static int visibleMap = -1; // debug item for viewing a map that an entity visits
    private int visbleMapRefresh = 0;
    private Color gridColor = new Color(0,0,0, 30);
    public World(int width, int height, int world_scale) {
        Util.deleteFolder("./logs/EntityLogs/");
        Util.deleteFolder("./logs/mapLogs/");
        Util.deleteFolder("./logs/taskLogs/");
        Util.deleteFolder("./logs/positionLogs/");
        Util.deleteFolder("./logs/worldLogs/");
        int entityCount = 0;
        int spawnLimit = 5;
        this.width = width;
        this.height = height;
        World.world_scale = world_scale;
        manager = new Manager();
        worldLogger = new Logger("./logs/worldLogs/WorldLog.txt");

        // entity initial setups
        for (int i = 0; i < spawnLimit; i++) {
            int x = Util.random(width);
            int y = Util.random(height);
            //x = 32*5;
            //y = 32*5;
            Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            manager.addEntity(x, y, this.width, this.height, world_scale, c, entityCount);
            entityCount += 1;
        }
        // tile map initial setup
        util.println("TileMap size: [" + Math.floor(height/world_scale) + "]" + "[" + Math.floor(width/world_scale) + "]");
        int count = 0;
        int food = 0;
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                //tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, World.world_scale,this.height,this.width, 0));
                if(y == 5 && x == 5)
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, World.world_scale,this.height,this.width, 2));
                else if(y == 1 && x == 1){
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, World.world_scale,this.height,this.width, 5));
                }else if(y == 1 && x == 3){
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, World.world_scale,this.height,this.width, 6));
                }
                else if (Math.random()*100 > 98 || count==0) {
                    tileMap.get(y).add(new Tile(x * world_scale, y * world_scale, count, World.world_scale, this.height, this.width, 1));
                    count++;
                }
                else
                    tileMap.get(y).add(new Tile(x*world_scale,y*world_scale,count, World.world_scale,this.height,this.width, 0));
                //*/
                count++;
            }
        }
        worldLogger.write("World info");
        worldLogger.writeNoTimestamp("World_scale: " + world_scale);
        worldLogger.writeNoTimestamp("World_h    : " + height);
        worldLogger.writeNoTimestamp("World_w    : " + width);
        worldLogger.writeNoTimestamp("num_spawns : " + spawnLimit);

    }//

    // there needs to be an initial entity update
    public void update(int seconds) {
        // TODO: When more sub tiles are added, then I can add updating all the tiles later
        // update entities

        // normal updates here
        if(seconds == 0 || (seconds > 0 && seconds % 10 == 0)){
            if(visbleMapRefresh == 0){
                if(subMaps.size() > 0) visibleMap = Util.random(subMaps.size());
                else visibleMap = 0;
                visbleMapRefresh = 67;
            }
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
        manager.update(tileMap, seconds);

        if(visbleMapRefresh > 0)
            visbleMapRefresh--;
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

        if(visibleMap >= 0)
            subMaps.get(visibleMap).render(g);

        manager.render(g);

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
        Map m = new Map(pos, tileMap, Game.sub_world_scale);
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
        worldLogger.write("number of iterations: " + Game.iterationCount);
        worldLogger.closeWriter();
        // shutdown each entity, currently just closes the logs
        manager.shutdown();
        // shutdown maps
        for(Map sub : subMaps){
            sub.shutdown();
        }
    }

    // for adding, removing from the entity ref map
    public static void editRefMap(String cmd, int pos, int id){
        manager.editMapRef(cmd,pos,id);
    }

    //
    public static Object callManager(String key, int x){
        return manager.call(key, x);
    }

}
