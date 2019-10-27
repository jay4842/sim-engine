package luna.world.objects;

import luna.util.ImageLoader;
import luna.util.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class HostileEncounter extends ObjectOfInterest {

    protected BufferedImage tileImage;

    // first we need to define the object and set the tiles
    public HostileEncounter(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        int subMapSize = 4;
        tileImage = ImageLoader.load("./res/tile/hostile.png");
        int count = 0;
        // make tiles here
        for(int y = 0; y < subMapSize; y++){
            tileMap.add(new ArrayList<Tile>());
            for(int x = 0; x < subMapSize; x++){
                this.tileMap.get(y).add(new Tile(xPos*world_scale, yPos*world_scale,count,world_scale,world_h,world_w,-1));
                count++;
            }
        }// end of making the map

    }

    // TODO: Spawn enemy entities
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
