package org.luna.core.entity.variants;

import org.luna.core.entity.Entity;
import org.luna.core.util.Animation;
import org.luna.core.util.ImageUtility;

import java.awt.image.BufferedImage;


public class MutationC extends Entity {

    public MutationC(int world_scale, int[] gps){
        super(world_scale, gps);
    }

    protected void setStats(){
        stats = new short[]{10,10,0,10,0,1,2,5,10,10};
        this.deathChance = .08f;
        this.replicationChance = .19f;
    }

    protected void setupImages(){
        int speed = 5;
        String rightPath = "res/entity/Right_slime_bob.png";
        BufferedImage img = ImageUtility.load(rightPath);
        img = ImageUtility.changeImageHue(175, img);
        // first lets make all the sheets for each animation
        spriteSheetMap.put("right", util.makeSpriteSheet(img,16,16,1,1));
        // alright now we can place these guys in the animation maps
        animationMap.put("right", new Animation(speed,spriteSheetMap.get("right")));

    }

    public Entity makeEntity(){
        return new MutationC(getScale(), new int[]{getGps()[0], getGps()[1], getGps()[2]});
    }
}
