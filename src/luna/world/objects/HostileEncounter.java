package luna.world.objects;

import luna.entity.Entity;
import luna.entity.unitelligent.BaseUnitelligent;
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

    // first we need to define the object and set the tiles
    public HostileEncounter(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        int subMapSize = 4;
        tileImage = ImageLoader.load("./res/tile/hostile.png");
        tileMap = Collections.synchronizedList(new ArrayList<List<Tile>>());

        int count = 0;
        boolean foodAdded = false;
        // make tiles here
        for(int y = 0; y < subMapSize; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < subMapSize; x++){
                if(y > 0 && x > 0 && !foodAdded){
                    this.tileMap.get(y).add(new Tile(xPos*world_scale, yPos*world_scale,count,world_scale,world_h,world_w,1));
                    foodAdded = true;
                }else{
                    this.tileMap.get(y).add(new Tile(xPos*world_scale, yPos*world_scale,count,world_scale,world_h,world_w,-1));
                }
                count++;
            }
        }// end of making the map
        //
        World.addMap(this.tileMap);
        this.tileMapPos = World.getMapListSize();
        if(this.tileMapPos > 0) this.tileMapPos-=1; // sub one for indexing
        this.type += "_" + this.tileMapPos;

        // now add hostiles
        int numHostiles = Util.random(3);
        for(int i = 0; i < numHostiles; i++){
            Entity hostile = new BaseUnitelligent(0, 0, tileMap.size(), tileMap.size(), world_scale, World.entities.size());
            hostile.setPosition(tileMapPos);
            hostile.setSubX(2*world_scale);
            hostile.setSubY(2*world_scale);
            World.entities.add(hostile);
        }


    }
    //
    // base render
    public void render(Graphics2D g) {
        // This will be done based on the type of object
        super.render(g);
        g.drawImage(tileImage,xPos+world_scale/6,yPos+world_scale/6,null);
    }

    public void update(int seconds) {
        // this will remove itself
        super.update(seconds);
    }

}
