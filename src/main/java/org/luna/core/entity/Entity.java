package org.luna.core.entity;

import org.luna.core.map.Tile;
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
    private static EntityUtil eUtil = new EntityUtil();
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

    private int moves = 0, move_wait = 10, max_moves = 25;

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

        stats = new int[]{10,10,0,10,0,1,2,3,10,10};
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy

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

    public void update(int step, List<List<Tile>> tilemap){
        animationMap.get(currentAnimation).runAnimation();
        taskManagement(step, tilemap);
        moveManagement(step, tilemap);
        energyManagement(step);
        currentAnimation = eUtil.getIntToStringDirectionMap().get(direction);
    }

    public void render(Graphics2D g){
        //g.setColor(Color.PINK);
        //g.fillRect(gps[1], gps[0], scale, scale);
        animationMap.get(currentAnimation).drawAnimation(g, gps[1], gps[0], scale, scale);
    }

    public void move(int direction){
        switch (direction) {
            case 0: {
                // move left
                this.gps[1] -= this.velocity;
                break;
            }
            case 1: {
                // move right
                this.gps[1] += this.velocity;
                break;
            }
            case 2: {
                // move up
                this.gps[0] -= this.velocity;
                break;
            }
            case 3: {
                // move down
                this.gps[0] += this.velocity;
                break;
            }
            default:
                break;
        }
    }

    // just cal cost
    private double calCost(int[] pos, int[] target){
        double vert = Math.pow(target[0] - pos[0], 2);
        double horz = Math.pow(target[1] - pos[1], 2);
        double sum = horz + vert;
        //System.out.println("sqrt(" + vert + " + " + horz + ") = " + result);
        return Math.sqrt(sum);
    }

    public void moveToTarget(int[] target){
        // TODO
    }

    public void moveManagement(int step, List<List<Tile>> tileMap){
        wander();
    }

    public void taskManagement(int step, List<List<Tile>> tileMap){

    }

    public void energyManagement(int step){

    }

    // walk around randomly
    public void wander(){
        if(type >= 5){
            //System.out.println("a unintelligent entity is calling wander");
            //System.out.println("Moves -> " + this.moves);
        }
        if(this.moves <= 0 && this.move_wait <= 0){
            //System.out.println("Set move items for entity");
            this.move_wait = 30;
            this.moves = (int)(Math.random()*max_moves) + 1;
            this.direction = (int)(Math.random()*4);
        }else if(this.moves > 0){
            //System.out.println("moving entity");
            // figure out which way we should move
            move(this.direction);
            this.moves--;
        }else{
            //System.out.println("sub move wait");
            this.move_wait--;
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

    //

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

    public Rectangle getBound(){
        return new Rectangle(gps[1], gps[0], scale, scale);
    }

    public Rectangle getSenseBound(){
        return new Rectangle(gps[1]-(scale), gps[0]-(scale), scale*stats[7],scale*stats[7]);
    }
}
