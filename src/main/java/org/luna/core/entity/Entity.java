package org.luna.core.entity;

import org.luna.core.entity.variants.MutationA;
import org.luna.core.map.Tile;
import org.luna.core.object.WorldObject;
import org.luna.core.reporting.Report;
import org.luna.core.util.Animation;
import org.luna.core.util.ImageUtility;
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
    private static String[] typeNames = new String[]{"base", "MutationA", "MutationB", "MutationC", "MutationD"};


    private static int counter = 0;
    private static int world_scale = -1;

    private static ImageUtility imgUtil = new ImageUtility();
    protected static Utility util = new Utility();
    private static EntityUtil eUtil = new EntityUtil();
    private int id;
    protected int type;
    private int groupID;
    private int[] gps;
    protected short[] stats;
    private TaskRef task;
    private Personality personality;
    //private Personality personality;
    //private List<Bond> bondList;
    //private List<Integer> inventory;
    private int direction = 0;
    private int scale;

    private int moves = 0;
    private int move_wait = 10;
    private int lastX, lastY;

    // animation stuff
    private Animation sprite;
    private String currentAnimation = "down";
    private String goal = "none";


    private static Color hpColor = new Color(255, 16, 38, 106);
    private static Color energyColor = new Color(255, 173, 0, 168);
    private static Color shadow = new Color(0,0,0, 54);

    protected float deathChance;
    protected float replicationChance;
    protected short replicationAge;
    private float energy, maxEnergy;
    private float replicationCost = .15f;
    protected float baseEnergyCost;

    protected int refreshStep; // every x step, stats are updated. ex: every x step energy is reduced

    private WorldObject targetObject = null;
    private boolean targetReached = false;
    private int interactingEntity = -1;

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

        sprite = new Animation(5,5); // Entity Animations will always have a set amount of frames
        setStats();
        personality = new Personality(); // TODO: have a personality creator helper class
        energy = stats[9];
        maxEnergy = energy;

        // image setup
    }

    protected void setStats(){
        short lifeSpan = 10;
        replicationAge = (short)(lifeSpan/3); // once an entity is 1/3 through its life it can replicate
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy, lifeSpanInTurns
        stats = new short[]{10,10,0,10,0,1,2,5,10,10,lifeSpan};
        this.deathChance = .15f;
        this.replicationChance = .15f;
        baseEnergyCost = 0.05f;
        refreshStep = -1;
    }

    public String update(int step, int turnSize, LunaMap map){
        if(refreshStep == -1)
            refreshStep = turnSize / 4;
        String output = "";
        lastX = gps[1];
        lastY = gps[0];

        if(isAlive()) {
            sprite.runAnimation();
            taskManagement(step, map);
            moveManagement(step, map);
            String result = energyManagement(step, map);
            if(result.length() > 0)output = result;

            currentAnimation = eUtil.getIntToStringDirectionMap().get(direction);

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
                output = getId() + ",Error, in entityRef";
            }
            // manage lifespan
            if(step % turnSize == 0 && step > 0){
                stats[10]--;
                updateState();
            }

        }

        return output;
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

            //g.drawImage(spriteSheetMap.get("right")[0],gps[1], gps[0], scale, scale, null );
            g.drawImage(imgUtil.getSpriteImage(currentAnimation + "_" + getTypeName(), sprite.getCount())
                    ,gps[1], gps[0], scale, scale, null );
            //animationMap.get(currentAnimation).drawAnimation(g, gps[1], gps[0], scale, scale);
            drawStats(g);
            g.setColor(Color.black);
            //g.drawString(getTypeName(),gps[1], gps[0]);
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
        if(makeStatusMessage().equals("hungry")){
            moveTowardsFood(step, map);
            if(targetObject == null)
                wander(step);
        }else
            wander(step);
        if(collision(map.getTileMap())){
            gps[0] = lastY;
            gps[1] = lastX;
        }
    }

    private void taskManagement(int step, LunaMap map){
        // Task management will handle entities current goals

    }

    private String energyManagement(int step, LunaMap map){
        String output = "";
        if(energy <= 0 && step % refreshStep == 0)
            stats[0]--;

        if(targetObjectReached() && targetObject.getType() == 1){
            if(!targetInMap(map)){
                releaseTarget();
            }else {
                output = "REMOVE,OBJECT," + targetObject.getGps()[0] + "," + targetObject.getGps()[1] + "," + targetObject.getListId();
                restoreEnergy(1.0f); // restore 100%% of energy
            }
        }

        return output;
    }

    // 0 left, 1 right, 2 up, 3 down
    public void moveTowardsFood(int step, LunaMap map){
        // use sense bound
        // - if food is found in its sense move to the first one it sees
        // - first find one food object, if found save its location
        // move until the food tile position matches your tile position

        // first get the objects
        if(targetObject == null) {
            boolean found = false;
            Rectangle sense = getSenseBound();
            // TODO: have looking start from adjacent cells, then to outer cells
            // loop through each tile and check for food in the each tile if
            int kernel = 1;
            int kx = gps[1]/world_scale;
            int ky = gps[0]/world_scale;
            int world_size = map.getObjectsInMap().size();
            // while kernel width is less than sense width
            while(kernel <= sense.width/world_scale && !found){
                for(int y = 0; y < kernel; y++){
                    for(int x = 0; x < kernel; x++){
                        if(((y == 0 || y == kernel-1) || (x == 0 || x == kernel-1)) && (ky >= 0 && kx >= 0 && ky+y < world_size && kx+x < world_size)){
                            for (WorldObject obj : map.getObjectsInMap().get(ky + y).get(kx + x)) {
                                if (obj.getType() == 1) {
                                    targetObject = obj;
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if(found) break;
                    }
                    if(found) break;
                }
                kernel+=2;
                kx--;
                ky--;
            }
            // reduce energy
        }else{
            if(gps[0]/scale != targetObject.getGps()[0]/scale && gps[1]/scale != targetObject.getGps()[1]/scale){

                // find which way we should move
                if(gps[0] < targetObject.getGps()[0])
                    move(3); // move down
                if(gps[0] > targetObject.getGps()[0])
                    move(2); // move up
                if(gps[1] < targetObject.getGps()[1])
                    move(1); // move right
                if(gps[1] > targetObject.getGps()[1])
                    move(0); // move left
            }else{
                targetReached = true;
            }
        }
    }

    // walk around randomly
    private void wander(int step){
        if(type >= 5){
            //System.out.println("a unintelligent entity is calling wander");
            //System.out.println("Moves -> " + this.moves);
        }
        if(this.moves <= 0 && this.move_wait <= 0){
            //System.out.println("Set move items for entity");
            this.move_wait = 30;
            int max_moves = (int)((25 * getPersonality().getAmbition()) / (.5));
            this.moves = (int)(Math.random()* max_moves) + 1;
            this.direction = (int)(Math.random()*4);
        }else if(this.moves > 0){
            //System.out.println("moving entity");
            // figure out which way we should move
            move(this.direction);
            decreaseEnergy(step, 1);
            this.moves--;
        }else{
            //System.out.println("sub move wait");
            this.move_wait--;
        }
    }

    private void drawStats(Graphics2D g){
        int hpWidth = (scale * stats[0]) / stats[1];
        int engWidth = (int) ((scale * energy) / maxEnergy);
        g.setColor(hpColor);
        g.fillRect(gps[1], gps[0] - ((scale/16)), hpWidth, scale/16);
        g.setColor(energyColor);
        g.fillRect(gps[1], gps[0], engWidth, scale/16);

    }

    private boolean collision(List<List<Tile>> tileMap){
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
        // update state, make changes to state variables I guess.
    }

    @Override
    public String makeStatusMessage(){
        if(hasLowEnergy())
            return "hungry";
        if(hasLowHp())
            return "hurt";
        // TODO: add additional status requests
        return "normal";
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
        return personality;
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

    // is dead returns true if health is less than or equal to 0, or its lifespan has expired and death chance
    public boolean isDead(){
        return (getStats()[0] <= 0 || (getStats()[10] <= 0 && Utility.getRnd().nextFloat() < deathChance));
    }

    public boolean replicate(){
        return (getStats()[10] <= replicationAge &&
                Utility.getRnd().nextFloat() < replicationChance &&
                energy >= maxEnergy*replicationCost);
    }

    public boolean isAlive(){
        return stats[0] > 0;
    }

    public boolean hasLowEnergy(){ // if energy is less than or equal to 30%;
        return energy <= (maxEnergy*.3);
    }

    public boolean hasLowHp(){ // if hp is less than or equal to 40% of hp
        return stats[0] <= (stats[1]*.4);
    }

    public int getScale(){
        return scale;
    }
    public Entity makeEntity(){
        energy -= maxEnergy*replicationCost; // require 15% of energy to replicate
        if(Utility.getRnd().nextFloat() < .3)
            return new MutationA(scale, new int[]{gps[0], gps[1], gps[2]});
        return new Entity(scale, new int[]{gps[0], gps[1], gps[2]});
    }

    public void restoreEnergy(float percent){
        energy += (maxEnergy * percent);
        if(energy > maxEnergy)
            energy = maxEnergy;
    }
    // get class name, will be used for rendering images
    public String getTypeName(){
        return typeNames[type];
    }

    public String makeReportLine(){
        return  id + "_" + gps[0] + "_" + gps[1] + "_" + gps[2];
    }

    private boolean targetObjectReached(){
        return targetReached;
    }

    private void releaseTarget(){
        targetObject = null;
        targetReached = false;
    }

    private boolean targetInMap(LunaMap map){
        int x = targetObject.getGps()[1]/scale;
        int y = targetObject.getGps()[0]/scale;
        int idx = targetObject.getListId();
        return map.isObjectInMap(y,x,idx);
    }

    public void setPersonality(Personality personality) {
        this.personality = personality;
    }

    private void decreaseEnergy(int step, float rate){
        if(step % refreshStep == 0 && step > 0) {
            energy -= (baseEnergyCost * rate);
        }
    }

    // interact with the maps entities
    // - if an entity sees another entitiy in its sights, depending on its extroversion it will try
    //   to interact with the other entity
    public void interact(int step, LunaMap map){

    }
}

