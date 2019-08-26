package luna.world.objects;

import java.awt.*;

public class Food extends InteractableObject{


    // food something
    public Food(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
    }
    //
    public void render(Graphics2D g){
        g.setColor(Color.orange);
        g.fillRect(xPos, yPos, 15,15);
    }
}
