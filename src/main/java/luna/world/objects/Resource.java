package luna.world.objects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Resource extends ObjectOfInterest{
    protected BufferedImage tileImage;
    private int respawnTimer = 0;
    private Color drawColor = Color.green;
    public Resource(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);

    }

    public Resource(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale, Color c) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        drawColor = c;
        setActive(true);
    }

    public void render(Graphics2D g){
        if(isActive()){
            g.setColor(drawColor);
            g.fillRect(xPos,yPos,world_scale/2, world_scale/2);
        }
    }

    public void update(int seconds){
        super.update(seconds);

        if(respawnTimer > 0)
            respawnTimer--;

        if(!isActive() && respawnTimer <= 0){
            setActive(true);
        }
    }

    // for marking inactive
    public void setActive(boolean active) {
        super.setActive(active);
        if(!isActive()){
            // resources take longer to respawn
            respawnTimer = 60 * 60;
        }
    }//
}
