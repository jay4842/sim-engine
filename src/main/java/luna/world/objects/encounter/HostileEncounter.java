package luna.world.objects.encounter;

import luna.entity.Entity;
import luna.entity.unitelligent.SmallLard;
import luna.entity.util.EntityManager;
import luna.main.Game;
import luna.util.ImageLoader;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.ObjectOfInterest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HostileEncounter extends ObjectOfInterest {

    protected BufferedImage tileImage;
    protected int respawnTimer = 0, respawnWaitTimer = 60*10;
    private int activeHostiles = 0;
    // first we need to define the object and set the tiles
    public HostileEncounter(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale, boolean test) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        int subMapSize = (Util.random(4)+1) + 4;
        tileImage = ImageLoader.load("./res/tile/hostile.png");
        tileMap = Collections.synchronizedList(new ArrayList<>());

        int count = 0;
        boolean foodAdded = false;
        // make tiles here

        for(int y = 0; y < subMapSize; y++){
            tileMap.add(new ArrayList<>());
            for(int x = 0; x < subMapSize; x++){
                if(y > 0 && x > 0 && !foodAdded){
                    tileMap.get(y).add(new Tile(x* Game.sub_world_scale, y*Game.sub_world_scale,count,Game.sub_world_scale,world_h,world_w,3, tileMapPos));
                    foodAdded = true;
                }else{
                    tileMap.get(y).add(new Tile(x*Game.sub_world_scale, y*Game.sub_world_scale,count,Game.sub_world_scale,world_h,world_w,-1, tileMapPos));
                }
                count++;
            }
        }// end of making the map
        // now add hostiles


    }

    // TODO: come up with a good formula for increasing difficulty
    //  - first try increasing numbers
    //  - then increasing hostile stats
    public void spawnHostiles(int seconds){
        // every 60 seconds the difficulty will increase (first number of hostiles)
        int numHostiles;
        if(seconds % 60 == 0 && seconds > 0){
            //Math.log1p() //
        }
        numHostiles = Util.random(1) + 1;
        for(int i = 0; i < numHostiles; i++){
            int id = (int)World.callManager("get_entitySize", null);
            //System.out.println(id);
            Entity hostile = new SmallLard(0, 0, tileMap.size(), tileMap.size(), getWorld_scale(), id);
            hostile.setPosition(tileMapPos);
            hostile.setSubX(2*getWorld_scale());
            hostile.setSubY(2*getWorld_scale());
            World.callManager("post_addEntity", hostile);
        }
        //System.exit(1);
    }
    //
    // base render
    public void render(Graphics2D g) {
        // This will be done based on the type of object
        super.render(g);
        if(isActive())
            g.drawImage(tileImage,xPos+getWorld_scale()/6,yPos+getWorld_scale()/6,null);

        //g.drawString("" + isActive() + " " + getTileMapPos(), xPos, yPos+(getWorld_scale()-5));
    }

    public void update(int seconds) {
        // this will remove itself
        super.update(seconds);

        if(!isActive() && respawnTimer <= 0){
            spawnHostiles(seconds);
            active = true;
        }

        int hostilesFound = 0;
        //System.out.println(World.subMaps.get(tileMapPos).getEntityRefs());
        //System.exit(1);
        Map<Integer, ArrayList<Integer>> refMap = (Map)World.callManager("get_entityRefMap", null);
        for(int i = 0; i < refMap.get(tileMapPos).size(); i++){
            int tmp = refMap.get(tileMapPos).get(i);
            Entity tmpEntity = (Entity)World.callManager("get_entity", tmp);
            if(tmpEntity.getType() >= 5 && tmpEntity.isAlive())
                hostilesFound++;
        }
        activeHostiles = hostilesFound;

        if (activeHostiles == 0 && respawnTimer == 0) {
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
        //System.out.println("num Hostiles = " + activeHostiles);
        return activeHostiles > 0;
    }
}
