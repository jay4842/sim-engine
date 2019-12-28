package org.luna.core.entity;

import org.luna.core.util.Animation;
import org.luna.core.util.State;
import org.luna.core.util.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity implements EntityActions, State {
    private static int counter = 0;
    private static Utility util = new Utility();
    private int id;
    private int type;
    private int groupID;
    private int[] gps;
    private int[] stats;
    private TaskRef task;
    private Personality personality;
    private List<Bond> bondList;
    private List<Integer> inventory;
    private int velocity = 2;
    private int direction = 0;
    private int scale;

    // animation stuff
    private Map<String, BufferedImage[]> spriteSheetMap;
    private Map<String, Animation> animationMap;
    private String currentAnimation = "down";

    public Entity(int world_scale, int[] gps){
        id = counter;
        counter++;
        this.scale = world_scale;
        this.gps = gps;
        this.type = 0;
        this.groupID = -1;
        this.task = new TaskRef();
        this.personality = new Personality();
        bondList = new ArrayList<>();
        inventory = new ArrayList<>(5);
        spriteSheetMap = new HashMap<>();
        animationMap = new HashMap<>();

        stats = new int[]{10,10,0,10,0,1,10,10};
        //hp, maxHp, xp, maxXp, lvl, dmg, hunger, maxHunger

        // image setup
        setupImages();
    }

    public void setupImages(){
        String leftPath = "res/entity/Left_slime_bob.png";
        String rightPath = "res/entity/Right_slime_bob.png";
        String upPath = "res/entity/Up_slime_bob.png";
        String DownPath = "res/entity/Down_slime_bob.png";
        String talkingPath = "res/emote/speaking_sheet.png";
        // first lets make all the sheets for each animation
        spriteSheetMap.put("left", util.makeSpriteSheet(leftPath,16,16,5,1));
        spriteSheetMap.put("right", util.makeSpriteSheet(rightPath,16,16,5,1));
        spriteSheetMap.put("up", util.makeSpriteSheet(upPath,16,16,5,1));
        spriteSheetMap.put("down", util.makeSpriteSheet(DownPath,16,16,5,1));
        spriteSheetMap.put("talking", util.makeSpriteSheet(talkingPath, 8,8,4,1));
        // alright now we can place these guys in the animation maps
        animationMap.put("left", new Animation(10,spriteSheetMap.get("left")));
        animationMap.put("right", new Animation(10,spriteSheetMap.get("right")));
        animationMap.put("up", new Animation(10,spriteSheetMap.get("up")));
        animationMap.put("down", new Animation(10,spriteSheetMap.get("down")));
        animationMap.put("talking", new Animation(10, spriteSheetMap.get("talking")));
    }

    public void update(){
        animationMap.get(currentAnimation.toLowerCase()).runAnimation();
    }

    public void render(Graphics2D g){
        //g.setColor(Color.PINK);
        //g.fillRect(gps[1], gps[0], scale, scale);
        animationMap.get(currentAnimation.toLowerCase()).drawAnimation(g, gps[1], gps[0], scale, scale);
    }

    public void move(int direction){
        switch (direction) {
            case 0: {
                // move left
                this.gps[0] -= this.velocity;
                break;
            }
            case 1: {
                // move right
                this.gps[0] += this.velocity;
                break;
            }
            case 2: {
                // move up
                this.gps[1] -= this.velocity;
                break;
            }
            case 3: {
                // move down
                this.gps[1] += this.velocity;
                break;
            }
            default:
                break;
        }
    }

    // state interface
    @Override
    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<>();
        state.put("GPS", gps);
        state.put("TASK", task);
        state.put("STATS", stats);
        state.put("ITEMS", inventory.toArray());
        state.put("BONDS", bondList.toArray());
        return state;
    }

    @Override
    public void updateState() {

    }

    // getters
    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getGroupID() {
        return groupID;
    }

    public int[] getGps() {
        return gps;
    }

    public int[] getStats() {
        return stats;
    }

    public TaskRef getTask() {
        return task;
    }

    public Personality getPersonality() {
        return personality;
    }

    public List<Bond> getBondList() {
        return bondList;
    }

    public List<Integer> getInventory() {
        return inventory;
    }

    public int getVelocity() {
        return velocity;
    }
}
