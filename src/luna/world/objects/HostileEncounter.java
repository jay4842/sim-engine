package luna.world.objects;

import luna.entity.Entity;
import luna.entity.unitelligent.BaseUnitelligent;
import luna.entity.unitelligent.SmallLard;
import luna.util.ImageLoader;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HostileEncounter extends ObjectOfInterest {

    protected BufferedImage tileImage;
    protected int respawnTimer = 0, respawnWaitTimer = 60*10;
    int activeHostiles = 0;
    // first we need to define the object and set the tiles
    public HostileEncounter(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        int subMapSize = 4;
        tileImage = ImageLoader.load("./res/tile/hostile.png");
        tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());

        int count = 0;
        boolean foodAdded = false;
        // make tiles here
        this.tileMapPos = World.getMapListSize();
        if(this.tileMapPos > 0) this.tileMapPos-=1; // sub one for indexing
        this.type += "_" + this.tileMapPos;

        for(int y = 0; y < subMapSize; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < subMapSize; x++){
                if(y > 0 && x > 0 && !foodAdded){
                    this.tileMap.get(y).add(new Tile(x*world_scale, y*world_scale,count,world_scale,world_h,world_w,3, this.tileMapPos));
                    foodAdded = true;
                }else{
                    this.tileMap.get(y).add(new Tile(x*world_scale, y*world_scale,count,world_scale,world_h,world_w,-1, this.tileMapPos));
                }
                count++;
            }
        }// end of making the map
        spawnHostiles();
        //
        World.addMap(this.tileMap, objectID);

        // now add hostiles


    }

    public void spawnHostiles(){
        int numHostiles = Util.random(2) + 1;
        for(int i = 0; i < numHostiles; i++){
            int id = World.entities.size();
            System.out.println(id);
            Entity hostile = new SmallLard(0, 0, tileMap.size(), tileMap.size(), world_scale, id);
            hostile.setPosition(tileMapPos);
            hostile.setSubX(2*world_scale);
            hostile.setSubY(2*world_scale);
            World.entities.add(hostile);
        }
        //System.exit(1);
    }
    //
    // base render
    public void render(Graphics2D g) {
        // This will be done based on the type of object
        super.render(g);
        if(isActive())
            g.drawImage(tileImage,xPos+world_scale/6,yPos+world_scale/6,null);
    }

    public void update(int seconds) {
        // this will remove itself
        super.update(seconds);

        if(!isActive() && respawnTimer <= 0){
            spawnHostiles();
            active = true;
        }

        int hostilesFound = 0;
        //System.out.println(World.subMaps.get(tileMapPos).getEntityRefs());
        //System.exit(1);
        // TODO: Fix bug: see Map.java
        if (World.subMaps.get(tileMapPos).getObjectID() == getObjectID()) {
            for (int[] ref : World.subMaps.get(tileMapPos).getEntityRefs()) {
                //System.out.println("First check " + ref[0] + " " + ref[1]);
                if (World.entities.get(ref[0]).isAlive() && ref[1] > 0){
                    //System.out.println("second check " + ref[0] + " " + ref[1]);
                    hostilesFound++;
                }
            }
        }
        activeHostiles = hostilesFound;

        if (activeHostiles == 0) {
            //System.out.println("inactive event");
            respawnTimer = respawnWaitTimer;
        }
        //System.exit(1);
        // other items
        if(respawnTimer > 0)
            respawnTimer--;

    }

    // based on active hostiles
    public boolean isActive(){
        return activeHostiles > 0;
    }
}
