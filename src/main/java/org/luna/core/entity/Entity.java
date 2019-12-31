package org.luna.core.entity;

import org.luna.core.entity.variants.MutationA;
import org.luna.core.map.Tile;
import org.luna.core.util.Animation;
import org.luna.core.util.State;
import org.luna.core.util.Utility;
import org.luna.core.map.*;
import org.luna.logic.service.EntityManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Entity implements EntityActions, State {
    private static int counter = 0;
    private static int world_scale = -1;
    protected static Utility util = new Utility();
    protected static EntityUtil eUtil = new EntityUtil();
    private int id;
    private int type;
    private int groupID;
    private int[] gps;
    protected short[] stats;
    private TaskRef task;
    //private Personality personality;
    //private List<Bond> bondList;
    //private List<Integer> inventory;
    private int direction = 0;
    private int scale;

    private int moves = 0;
    private int move_wait = 10;
    private int lastX, lastY;

    // animation stuff
    protected Map<String, BufferedImage[]> spriteSheetMap;
    protected Map<String, Animation> animationMap;
    private String currentAnimation = "down";

    private static Color hpColor = new Color(255, 16, 38, 106);
    private static Color energyColor = new Color(255, 243, 221, 106);
    private static Color shadow = new Color(0,0,0, 54);

    protected float deathChance;
    protected float replicationChance;

    public Entity(int world_scale, int[] gps){
        if(Entity.world_scale == -1)
            Entity.world_scale = world_scale;
        id = counter;
        counter++;
        this.scale = world_scale;
        this.gps = gps;
        this.type = 0;
        this.groupID = -1;
        this.task = new TaskRef();
        //this.personality = new Personality();
        //bondList = new ArrayList<>();
        //inventory = new ArrayList<>(5);
        spriteSheetMap = new HashMap<>();
        animationMap = new HashMap<>();

        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy
        setStats();
        // image setup
        if(spriteSheetMap.size() <= 0)setupImages();
    }

    protected void setStats(){
        stats = new short[]{10,10,0,10,0,1,2,5,10,10};
        this.deathChance = .15f;
        this.replicationChance = .15f;
    }

    protected void setupImages(){
        int speed = 5;
        String leftPath = "res/entity/Left_slime_bob.png";
        String rightPath = "res/entity/Right_slime_bob.png";
        String upPath = "res/entity/Up_slime_bob.png";
        String DownPath = "res/entity/Down_slime_bob.png";
        String talkingPath = "res/emote/speaking_sheet.png";
        // first lets make all the sheets for each animation
        //spriteSheetMap.put("left", util.makeSpriteSheet(leftPath,16,16,5,1));
        spriteSheetMap.put("right", util.makeSpriteSheet(rightPath,16,16,5,1));
        //spriteSheetMap.put("up", util.makeSpriteSheet(upPath,16,16,5,1));
        //spriteSheetMap.put("down", util.makeSpriteSheet(DownPath,16,16,5,1));
        //spriteSheetMap.put("talking", util.makeSpriteSheet(talkingPath, 8,8,4,1));
        // alright now we can place these guys in the animation maps
        //animationMap.put("left", new Animation(speed,spriteSheetMap.get("left")));
        animationMap.put("right", new Animation(speed,spriteSheetMap.get("right")));
        //animationMap.put("up", new Animation(speed,spriteSheetMap.get("up")));
        //animationMap.put("down", new Animation(speed,spriteSheetMap.get("down")));
        //animationMap.put("talking", new Animation(speed, spriteSheetMap.get("talking")));

        /*for(int i = 0; i < 1; i++){
            for(int x = 0; x < 60*10; x++){
                animationMap.get( eUtil.getIntToStringDirectionMap().get(i)).runAnimation();
            }
        }*/
    }

    public void update(int step, LunaMap map){
        lastX = gps[1];
        lastY = gps[0];

        if(isAlive()) {
            //animationMap.get(currentAnimation).runAnimation();
            taskManagement(step, map);
            moveManagement(step, map);
            energyManagement(step);

            //currentAnimation = eUtil.getIntToStringDirectionMap().get(direction);

            // Manage entity ref map before returning the updated refMap
            if (EntityManager.entityRef.size() > 0 && EntityManager.entityRef.get(gps[2]).size() > 0) {
                // look for it self and update the int[]
                for (int i = 0; i < EntityManager.entityRef.get(gps[2]).size(); i++) {
                    if (EntityManager.entityRef.get(gps[2]).get(i)[0] == id) {
                        EntityManager.entityRef.get(gps[2]).get(i)[1] = gps[0] / world_scale;
                        EntityManager.entityRef.get(gps[2]).get(i)[2] = gps[1] / world_scale;
                        break; // stop editing
                    }
                }
            } else if (EntityManager.entityRef.get(gps[2]).size() <= 0) {
                EntityManager.entityRef.get(gps[2]).add(new Integer[]{id, gps[0] / world_scale, gps[1] / world_scale, gps[2]});
            } else {
                System.out.println("error saving entity to entityRef, the list at gps [y,x," + gps[2] + "] does not exist");
            }

        }
    }

    public void deleteSelfFromRef(){
        if(EntityManager.entityRef.size() > 0 && EntityManager.entityRef.get(gps[2]).size() > 0){
            // look for it self and update the int[]
            for(int i = 0; i < EntityManager.entityRef.get(gps[2]).size(); i++){
                if(EntityManager.entityRef.get(gps[2]).get(i)[0] == id){
                    EntityManager.entityRef.get(gps[2]).remove(i);
                    break;
                }
            }
        }

        // now remove self from list
    }

    public void render(Graphics2D g){
        if(isAlive()) {
            //g.setColor(Color.PINK);
            //g.fillRect(gps[1], gps[0], scale, scale);
            g.setColor(shadow);
            Rectangle sense = getSenseBound();
            //g.fillRect(sense.x, sense.y, sense.width, sense.height);

            g.drawImage(spriteSheetMap.get("right")[0],gps[1], gps[0], scale, scale, null );
            //animationMap.get(currentAnimation).drawAnimation(g, gps[1], gps[0], scale, scale);
            //drawStats(g);
        }
    }

    public void move(int direction){
        switch (direction) {
            case 0: {
                // move left
                this.gps[1] -= stats[6];
                break;
            }
            case 1: {
                // move right
                this.gps[1] += stats[6];
                break;
            }
            case 2: {
                // move up
                this.gps[0] -= stats[6];
                break;
            }
            case 3: {
                // move down
                this.gps[0] += stats[6];
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

    public void moveManagement(int step, LunaMap map){
        wander();
        if(collision(map.getTileMap())){
            gps[0] = lastY;
            gps[1] = lastX;
        }
    }

    public void taskManagement(int step, LunaMap map){

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
            int max_moves = 25;
            this.moves = (int)(Math.random()* max_moves) + 1;
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

    public void drawStats(Graphics2D g){
        int hpWidth = (scale * stats[0]) / stats[1];
        int engWidth = (scale * stats[8]) / stats[9];
        g.setColor(hpColor);
        g.fillRect(gps[1], gps[0] - ((scale/16)+1), hpWidth, scale/16);
        g.setColor(energyColor);
        g.fillRect(gps[1], gps[0], engWidth, scale/16);

    }

    public boolean collision(List<List<Tile>> tileMap){
        int x = gps[1];
        int y = gps[0];
        return x <= 0 || y <= 0 || x >= (tileMap.get(0).size()-1) * world_scale || y >= (tileMap.size()-1) * world_scale;
    }

    // state interface
    @Override
    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<>();
        state.put("GPS", gps);
        state.put("TASK", task);
        state.put("STATS", stats);
        //state.put("ITEMS", inventory.toArray());
        //state.put("BONDS", bondList.toArray());
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

    public short[] getStats() {
        return stats;
    }

    public TaskRef getTask() {
        return task;
    }

    public Personality getPersonality() {
        return null;
    }

    public List<Bond> getBondList() {
        return null;
    }

    public List<Integer> getInventory() {
        return null;
    }

    public int getVelocity() {
        return stats[7];
    }

    public Rectangle getBound(){
        return new Rectangle(gps[1], gps[0], scale, scale);
    }

    public Rectangle getSenseBound(){
        int start = ((stats[7] - 1) / 2) - (stats[7] - 1);
        return new Rectangle(gps[1]+(start*scale), gps[0]+(start*scale), scale*stats[7],scale*stats[7]);
    }

    public boolean isDead(){
        return (Utility.getRnd().nextFloat() < deathChance);
    }

    public boolean replicate(){
        return (Utility.getRnd().nextFloat() < replicationChance);
    }

    public boolean isAlive(){
        return stats[0] > 0;
    }

    public int getScale(){
        return scale;
    }
    public Entity makeEntity(){
        if(Utility.getRnd().nextFloat() < .3)
            return new MutationA(scale, new int[]{gps[0], gps[1], gps[2]});
        return new Entity(scale, new int[]{gps[0], gps[1], gps[2]});
    }
}
