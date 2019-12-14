package luna.entity.unitelligent;

import luna.entity.Entity;
import luna.util.Animation;
import luna.util.Logger;
import luna.util.Util;

import java.awt.*;

/*
*  Unintelligent entities, like hostiles. (NPC type characters that are more static)
*
* */
public class BaseUnitelligent extends Entity {
    public BaseUnitelligent(int world_w, int world_h, int id) {
        super(world_w, world_h, id);
    }

    public BaseUnitelligent(int x, int y, int world_w, int world_h, int world_scale, int id) {
        super(x, y, world_w, world_h, world_scale, id);
    }

    public BaseUnitelligent(int x, int y, int world_w, int world_h, int world_scale, Color c, int id) {
        super(x, y, world_w, world_h, world_scale, c, id);
    }

    // overrides
    //  - Should only have to override the set stats make images

    public void set_stats(){
        this.type = 5;
        this.logger = new Logger("./logs/EntityLogs/entity_" + this.getEntityID() + ".txt");
        this.taskLogger = new Logger("./logs/taskLogs/entity_" + this.getEntityID() + "_TaskLog.txt");
        this.positionLogger = new Logger("./logs/positionLogs/entity_" + this.getEntityID() + "_posLog.txt");
        logger.write("init stats");
        this.max_hp = (int)(Math.random() * 3) + 1;
        this.dmg = (int)(Math.random() * 1) + 1;
        this.hp = max_hp;
        this.xp = 0;
        this.max_xp = 3;
        this.drop_xp = 1;
        this.max_hunger = 20 + (int)(Math.random()*25);
        this.hunger = this.max_hunger;
        this.hunger_loss_rate = (int)(Math.random()*1) + 1;
        //
        this.currTileX = x / world_scale;
        this.currTileY = y / world_scale;
        subTileX = 0;
        subTileY = 0;
        subX = -1;
        subY = -1;

        logger.write("Entity " + this.getEntityID());
        logger.write("hp:  " + this.max_hp);
        logger.write("dmg: " + this.dmg);
        logger.write("pos: [" + this.currTileY + " " + this.currTileY + "]");

    }

    // some other setups
    public void makeImages(){
        // These are just the idle images/ moving images
        // - other frames will be added later
        logger.write("Init images");
        String leftPath = "res/hostile_a_left_bob.png";
        String rightPath = "res/hostile_a_right_bob.png";
        String upPath = "res/hostile_a_up_bob.png";
        String DownPath = "res/hostile_a_down_bob.png";
        String talkingPath = "res/emote/speaking_sheet.png";
        // first lets make all the sheets for each animation
        spriteSheetMap.put("Left", Util.makeSpriteSheet(leftPath,16,16,5,1));
        spriteSheetMap.put("Right", Util.makeSpriteSheet(rightPath,16,16,5,1));
        spriteSheetMap.put("Up", Util.makeSpriteSheet(upPath,16,16,5,1));
        spriteSheetMap.put("Down", Util.makeSpriteSheet(DownPath,16,16,5,1));
        spriteSheetMap.put("Talking", Util.makeSpriteSheet(talkingPath, 8,8,4,1));
        // alright now we can place these guys in the animation maps
        animationMap.put("Left", new Animation(10,spriteSheetMap.get("Left")));
        animationMap.put("Right", new Animation(10,spriteSheetMap.get("Right")));
        animationMap.put("Up", new Animation(10,spriteSheetMap.get("Up")));
        animationMap.put("Down", new Animation(10,spriteSheetMap.get("Down")));
        animationMap.put("Talking", new Animation(10, spriteSheetMap.get("Talking")));
        //
        // now make the direction maps
    }

    public String toString(){
        return  "Entity      :: " + this.getEntityID() +
                "\nHP          :: " + this.max_hp +
                "\nLevel       :: " + this.level +
                "\nHunger      :: " + this.hunger +
                "\nMapPos      :: " + this.position +
                "\nCurrentTask :: " + getCurrentTask().getGoal() +
                "\nMoves?      :: " + getMoves() +
                "\nTarget?     :: " + getTargetEntityID() +
                "\n";

    }

}
