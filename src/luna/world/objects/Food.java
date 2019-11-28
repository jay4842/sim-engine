package luna.world.objects;

import java.awt.*;

public class Food extends InteractableObject{

    // item type = 0

    // food something
    int world_scale;
    Color foodColor = new Color(255, 51,0, 150);
    public Food(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        this.world_scale = world_scale;
    }
    //
    @Override
    public void render(Graphics2D g){
        g.setColor(foodColor);
        g.fillRect(xPos, yPos, world_scale/2,world_scale/2);
    }

    public void render(Graphics2D g, int x, int y){
        g.setColor(foodColor);
        g.fillRect(x + xPos, y + yPos, world_scale/2,world_scale/2);
    }

    @Override
    public void update(int seconds){
        super.update(seconds);
        // add to the amt over time


    }
}
