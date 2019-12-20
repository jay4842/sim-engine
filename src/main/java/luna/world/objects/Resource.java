package luna.world.objects;

import luna.util.Util;
import luna.world.objects.item.Item;
import luna.world.objects.item.ItemMaker;
import luna.world.util.ObjectManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Resource extends ObjectOfInterest{
    protected BufferedImage tileImage;
    private int respawnTimer = 0;
    private int amount;
    private Color drawColor = Color.green;
    public Resource(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        amount = Util.random(25) + 10;
        setActive(true);
    }

    public Resource(int xPos, int yPos, String type, int objectID, int world_h, int world_w, int world_scale, Color c) {
        super(xPos, yPos, type, objectID, world_h, world_w, world_scale);
        drawColor = c;
        amount = Util.random(25) + 10;
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
            amount = Util.random(25) + 10;
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

    // make a resource item based on the tile type
    // - considers amount to receive too.
    public Item harvest(){
        // create an item from an item ref
        String [] split = type.split("_");
        String itemType = split[0] + "_" + split[1];
        Item resource = ObjectManager.createItem(itemType);
        int amount = (int)(this.amount * .3);
        if(this.amount-amount < 0) {
            resource.setAmount(this.amount);
            this.amount = 0;
            setActive(false);

        }else{
            this.amount-=amount;
            resource.setAmount(amount);
        }

        System.out.println(ObjectManager.itemRefs.get(resource.getItemID()).getNamespace() + " created");
        return resource;
    }
}
