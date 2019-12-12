package luna.world;

import luna.entity.Entity;
import luna.main.Game;
import luna.util.Logger;
import luna.util.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// This class will hold the 2D tile arrays and also manage the entities within them.
//  It should be the only way to correctly and efficiently manage sub maps
public class Map{

    protected List<List<Tile>> tileMap;
    protected List<int[]> entityRefs;
    int overwordX, overworldY, width, height, world_scale;
    int mapPos = -1;
    boolean finalInit = false;
    int objectID = -1; // to help tie a map to an object (like an encounter)
    Color shadow = new Color(0,0,0,80);
    Logger logger;

    // Constructor where the tile map is made using the default 5x5 size
    public Map(int mapPos, int world_scale){
        this.world_scale = world_scale;
        logger = new Logger("./logs/mapLogs/map_" + mapPos + ".txt");
        width = 5;
        height = 5;
        this.mapPos = mapPos;
        int count = 0;

        entityRefs = Collections.synchronizedList(new ArrayList<int[]>());
        tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
        logger.write("Creating Map...");
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                Tile tile = new Tile(x*world_scale, y*world_scale,count,
                        world_scale,height*world_scale,width*world_scale,-1, mapPos);
                logger.writeNoTimestamp(tile.toString());
                this.tileMap.get(y).add(tile);
                count++;
            }
        }// end of making the map
        logger.writeNoTimestamp("--------------------------------");

    }

    // constructor with size parameter
    public Map(int mapPos,int mapSize, int world_scale){
        this.world_scale = world_scale;
        width = mapSize;
        height = mapSize;
        this.mapPos = mapPos;
        int count = 0;
        logger = new Logger("./logs/mapLogs/map_" + mapPos + ".txt");
        entityRefs = Collections.synchronizedList(new ArrayList<int[]>());
        tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());
        logger.write("Creating Map...");
        for(int y = 0; y < height/world_scale; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < width/world_scale; x++){
                Tile tile = new Tile(x*world_scale, y*world_scale,count,
                        world_scale,height*world_scale,width*world_scale,-1, mapPos);
                logger.writeNoTimestamp(tile.toString());
                this.tileMap.get(y).add(tile);
                count++;
            }
        }// end of making the map
        logger.writeNoTimestamp("--------------------------------");
    }

    // constructor where the tilemap is passed
    public Map(int mapPos, List<List<Tile>> tileMap, int world_scale){
        this.tileMap = tileMap;
        this.mapPos = mapPos;
        this.world_scale = world_scale;
        this.height = tileMap.size();
        this.width = tileMap.get(0).size();
        entityRefs = Collections.synchronizedList(new ArrayList<int[]>());
        logger = new Logger("./logs/mapLogs/map_" + mapPos + ".txt");
        logger.write("Creating Map...");
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                logger.writeNoTimestamp(tileMap.get(y).get(x).toString());
            }
        }
        logger.writeNoTimestamp("--------------------------------");
    }//

    // render
    public void render(Graphics2D g){
        // debug function for now, will be called later when I implement an observer roll

        g.setColor(shadow);
        g.fillRect(getRenderXStart(),getRenderYStart(),width*(Game.sub_world_scale), height*(Game.sub_world_scale));

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
        if(!finalInit){
            makeEntityRefs();
            finalInit = true;
        }
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
    public int getRenderXStart(){return Game.ACTUAL_WIDTH-(width*(Game.sub_world_scale))-10;}
    public int getRenderYStart(){return 10;}

    // save operations will occur here
    public void shutdown(){
        this.logger.closeWriter();
    }

    public List<int[]> getEntityRefs() {
        return entityRefs;
    }

    public void setEntityRefs(List<int[]> entityRefs) {
        this.entityRefs = entityRefs;
    }

    public int getObjectID() {
        return objectID;
    }

    public void setObjectID(int objectID) {
        this.objectID = objectID;
    }

    public void makeEntityRefs(){
        entityRefs.clear();
        // update entity refs
        // - This is an iterative process but should be quick due to it only occurring in sub maps
        for(int y = 0; y < World.subMaps.get(mapPos).getTileMap().size(); y++) {
            for (int x = 0; x < World.subMaps.get(mapPos).getTileMap().get(0).size(); x++) {
                System.out.println("y " + y + " x " + x);
                System.out.println("h " + height + " w " + width);
                System.out.println("map? " + mapPos);
                System.out.println(World.subMaps.get(mapPos).getTileMap().size());
                System.out.println(World.subMaps.get(mapPos).getTileMap().get(y).size());
                System.out.println(World.subMaps.get(mapPos).getTileMap().get(y).get(x).getEntitiesInTile().size());

                for(int i = 0; i < World.subMaps.get(mapPos).getTileMap().get(y).get(x).getEntitiesInTile().size(); i++){
                    Entity tmp = World.subMaps.get(mapPos).getTileMap().get(y).get(x).getEntitiesInTile().get(i);
                    int[] ref = new int[]{tmp.getEntityID(), tmp.getType()};
                    if(entityRefs.size() > 0) {
                        for (int j = 0; j < entityRefs.size(); j++) {
                            if (ref[0] != entityRefs.get(j)[0] && ref[1] != entityRefs.get(j)[1]) {
                                //System.out.println(ref[0] + " " + ref[1]);
                                entityRefs.add(ref);
                            }
                        }
                    }else{
                        //System.out.println(ref[0] + " " + ref[1]);
                        entityRefs.add(ref);
                    }
                }
            }
            /*for(int i = 0; i < entityRefs.size(); i++)
                System.out.println(entityRefs.get(i)[0] + " " + entityRefs.get(i)[1]);

            System.out.println("updated ref size: " + entityRefs.size());
            //System.exit(1);*/
        }//
    }
}
