package luna.entity.unitelligent;

import luna.entity.Entity;
import luna.entity.Task;
import luna.util.Animation;
import luna.util.Logger;
import luna.util.Tile;
import luna.util.Util;

import java.awt.*;
import java.util.List;

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
        this.logger = new Logger("./logs/EntityLogs/entity_" + this.getEntityID() + ".txt");
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
        this.currentTask = new Task(new int[]{currTileX, currTileY}, 0,this.getEntityID());

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
        // first lets make all the sheets for each animation
        spriteSheetMap.put("Left", Util.makeSpriteSheet(leftPath,16,16,5,1));
        spriteSheetMap.put("Right", Util.makeSpriteSheet(rightPath,16,16,5,1));
        spriteSheetMap.put("Up", Util.makeSpriteSheet(upPath,16,16,5,1));
        spriteSheetMap.put("Down", Util.makeSpriteSheet(DownPath,16,16,5,1));
        // alright now we can place these guys in the animation maps
        animationMap.put("Left", new Animation(10,spriteSheetMap.get("Left")));
        animationMap.put("Right", new Animation(10,spriteSheetMap.get("Right")));
        animationMap.put("Up", new Animation(10,spriteSheetMap.get("Up")));
        animationMap.put("Down", new Animation(10,spriteSheetMap.get("Down")));
        //
        // now make the direction maps
    }

    // TODO: handle targeting
    // TODO: add hunger task find
    public void taskManagement(List<List<Tile>> tileMap, int seconds){
        // nothing for now
        if(!currentTask.isTaskSet()) {
            if(position == -1){
                currentTask.setStartPos(new int[]{currTileY,currTileX});
            }else{
                currentTask.setStartPos(new int[]{subTileY,subTileX});
            }

            currentTask.makeTask(tileMap, seconds);
        }

        // hunger goal can override the rest goal, due to hunger affecting health as well
        if(this.hunger < this.max_hunger*.50 && currentTask.getGoal() != 1) {
            //
            currentTask.setGoal(1);
        }
        if(this.hp < this.max_hp*.50 && currentTask.getGoal() != 2 && currentTask.getGoal() != 1){
            currentTask.setGoal(2);
        }

        if(position == -1 && currentTask.isTaskFinished(new int[]{currTileY,currTileX}, seconds)){
            // finish a hunger quest
            if(currentTask.getGoal() == 1) {
                currentTask.setGoal(4);
                hunger = max_hunger;
            }
            // others
            if(currentTask.getGoal() == 7 && position == -1){ // make sure this assignment only happens once
                // now we need to move around only in our sub map
                //
                position = currentTask.getTargetMapPos();
                subX = 5; // setting to 0 could mess with collision
                subY = 5;

                direction = Util.stringToIntDirectionMap.get("down");
            }
        }else if(position != -1){ // TODO: fix task finished issue
            if(currentTask.isTaskFinished(new int[]{subTileY, subTileX}, seconds)){
                if(currentTask.getGoal() == 1) {
                    System.out.println("Sub map completed [" + getEntityID() + "]");
                    currentTask.setGoal(4);
                    hunger = max_hunger;
                }
            }
        }
    }



}
