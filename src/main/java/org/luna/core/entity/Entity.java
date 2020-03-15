package org.luna.core.entity;

import org.luna.core.entity.variants.MutationA;
import org.luna.core.item.Item;
import org.luna.core.map.Tile;
import org.luna.core.object.WorldObject;
import org.luna.core.util.Animation;
import org.luna.core.util.ImageUtility;
import org.luna.core.util.State;
import org.luna.core.util.Utility;
import org.luna.core.map.*;
import org.luna.logic.service.EntityManager;
import org.luna.core.reporting.Report;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Entity implements EntityActions, State {
    public static String[] typeNames = new String[]{"base", "MutationA", "MutationB", "MutationC", "MutationD"};
    private Report entityLog;

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
    //private List<Bond> bondList;
    private Map<Integer, Item> inventory; // unique id, namespace
    private int carryingCapacity;
    private int direction = 0;
    private int scale;

    private int moves = 0;
    private int move_wait = 10;
    private int lastX, lastY;
    private int tileX, tileY;

    private int sim_id;

    // animation stuff
    private Animation sprite;
    private String currentAnimation = "down";
    private String goal = "none"; // this will be used to find things in the world
    private float[] needs; // hold the basic needs of the entity

    private static Color hpColor = new Color(255, 16, 38, 106);
    private static Color energyColor = new Color(255, 173, 0, 168);
    private static Color shadow = new Color(0,0,0, 54);

    protected float deathChance;
    protected float replicationChance;
    protected short replicationAge;
    private float energy, maxEnergy;
    private float exhaustion; // sleep need
    private float thirst;
    // TODO: add interaction needs
    private int dailyInteractionNeed; // these two will affect social needs
    private int dailyInteractionCount;
    private int minPositiveBonds; // entities will have a desired minimum amount of friends
    private static int interactionTimerMax = -1;
    private int interactionTimer = 0;

    private float replicationCost = .15f;
    protected float baseEnergyCost;
    protected float idleEnergyCostRate;

    protected int refreshStep; // every x step, stats are updated. ex: every x step energy is reduced

    private WorldObject targetObject = null;
    private boolean targetReached = false;
    private int interactingEntity = -1;
    private int refListId = -1;
    private int locked = 0; // if lock is greater than 0, only certain actions can be done


    public Entity(int world_scale, int[] gps, int sim){
        this.sim_id = sim;
        if(Entity.world_scale == -1)
            Entity.world_scale = world_scale;
        id = counter;
        counter++;
        this.entityLog = new Report("logs/entity/sim_" + sim + "/entity_" + id + ".txt");
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
        exhaustion = 0f;
        thirst = 0f;
        // Physiological need (tied to hunger, sleep, shelter, basic well being)
        // Safety need (security in ones society, TBD)
        // social need (love, affection, friendship, acceptance)
        // Esteem need (self esteem, self-respect, confidence on ones self)
        // self-actualization need (self development need)
        needs = new float[]{0f,0f,0f,0f,0f};
        carryingCapacity = 5;
        inventory = new HashMap<>();
        personality = new Personality(); // TODO: have a personality creator helper class
        energy = stats[9];
        maxEnergy = energy;
        idleEnergyCostRate = 0.01f;

        dailyInteractionNeed = 1;
        dailyInteractionCount = 0;
        minPositiveBonds = 1;
        // image setup
    }

    protected void setStats(){
        short lifeSpan = 120;
        replicationAge = (short)(lifeSpan/3); // once an entity is 1/3 through its life it can replicate
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy, lifeSpanInTurns, replications, TODO: add strength
        this.deathChance = .15f;
        this.replicationChance = .15f;
        baseEnergyCost = 0.05f;
        stats = new short[]{10,10,0,10,0,1,2,5,10,10,lifeSpan,2};
        refreshStep = -1;
    }

    public List<String> update(int step, int turnSize, LunaMap map, int daySize){
        List<String> outList = new ArrayList<>();
        String result = "";
        if(refreshStep == -1)
            refreshStep = turnSize;// / 4;
        if(interactionTimerMax == -1)
            interactionTimerMax = turnSize*2;

        if(step % daySize == 0){
            // daily counters need to refresh
            dailyInteractionCount = 0;
        }
        lastX = gps[1];
        lastY = gps[0];
        tileX = gps[1]/world_scale;
        tileY = gps[0]/world_scale;

        if(isAlive()) {
            sprite.runAnimation();
            // call task management
            result = taskManagement(step, turnSize, map, daySize);
            if(result.length() > 0)outList.add(getId() + "," + result);
            // call move management
            moveManagement(step, map);
            result = energyManagement(step, turnSize, map, daySize);
            if(result.length() > 0)outList.add(getId() + "," + result);
            // call need management
            result = needManagement();
            if(result.length() > 0)outList.add(getId() + "," + result);

            currentAnimation = eUtil.getIntToStringDirectionMap().get(direction);

            // Manage entity ref map before returning the updated refMap
            int newTileX = gps[1]/world_scale;
            int newTileY = gps[0]/world_scale;
            if(refListId == -1){
                refListId = EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).size();
                tileX = newTileX;
                tileY = newTileY;
                EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).add(new Integer[]{id, tileY, tileX});
            }else{
                // check if we need to remove the old one and add a new one somewhere else
                if(newTileX != tileX && newTileY != tileY && EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).size() > 0){
                    // assign a new listRef
                    deleteSelfFromRef();
                    tileX = newTileX;
                    tileY = newTileY;
                    refListId = EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).size();
                    EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).add(new Integer[]{id, tileY, tileX});
                }
            }
            // manage lifespan
            if(step % turnSize == 0 && step > 0){
                stats[10]--;
                updateState();
            }

            // timers
            if(interactionTimer > 0)
                interactionTimer--;

        }

        // log to report
        entityLog.writeLn(makeEntityReportLine(), step);
        return outList;
    }

    // There can be index errors if you just use the RefID, sometimes entities might move a little too fast
    //  and miss label its refID.
    // This guy checks if the refID is valid before using it, and if it's not it will remove itself safely
    public void deleteSelfFromRef(){
        if(EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).size() > 0 && refListId < EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).size())
            EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).remove(refListId);
        else{
            // then we need to loop till we find our self
            for(int i = 0; i < EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).size(); i++){
                if(EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).get(i)[0] == id) {
                    EntityManager.entityRef.get(gps[2]).get(tileY).get(tileX).remove(i);
                    break;
                }
            }//
        }
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
                    ,gps[1], gps[0], scale, scale, null);
            //animationMap.get(currentAnimation).drawAnimation(g, gps[1], gps[0], scale, scale);
            drawStats(g);
            g.setColor(Color.black);
            //g.drawString(getTypeName(),gps[1], gps[0]);
        }
    }

    public void move(int direction, int step){
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
        decreaseEnergy(step, 1);
    }

    // just cal cost
    private double calCost(int[] pos, int[] target){
        double vert = Math.pow(target[0] - pos[0], 2);
        double horz = Math.pow(target[1] - pos[1], 2);
        double sum = horz + vert;
        //System.out.println("sqrt(" + vert + " + " + horz + ") = " + result);
        return Math.sqrt(sum);
    }

    // TODO: smarter movement
    //  - currently the entities just wander around, and if they happen to find food where
    //   then they keep wandering.
    //  - This will also depend on how I want entities to start making camps or saving a home cord
    //  - Movement also will depend on task management.
    //  - Having a preference of tile to be on too
    //    - entities will have areas they favor more, energy drain rates will differ from tile to tile
    //    - The preference will be set depending on the tile the entity is born on
    public void moveManagement(int step, LunaMap map){
        // handle goal based moves here
        if(goal.contains("find_food")){
            if(!targetReached)
                targetReached = moveTowardsFood(step, map);
            if(targetObject == null)
                wander(step);

        }else
            wander(step);

        if(collision(map.getTileMap())){
            gps[0] = lastY;
            gps[1] = lastX;
        }
    }

    // TODO: defining task vs doing something based on status?
    //  - so, entities have instinct movements, like finding food if hungry, or staying away
    //    from an enemy.
    //  - Tasks however are more intelligent
    //    - Example: Gather food to save for later
    //    -          Explore an area the group has not seen yet
    //    - They will also extend the functionality of a group, so it will be dependent on the group feature.
    //  - Tasks will also extend how entities interact with the map
    //    - they will be able to build, grow food, explore caves, encounter other entities etc.
    private String taskManagement(int step, int turnSize, LunaMap map, int daySize){
        String output = "";
        // Task management will handle entities current goals
        // - Here we set goals based on needs
        // Hunger
        if(makeStatusMessage().contains("hungry") && goal.equals("none")){
            int idx = itemTypeInInventory("food");
            if(idx > -1){
                // TODO: restore energy based on the food that was consumed
                // then just eat here
                inventory.get(idx).subAmount(1);
                if(inventory.get(idx).getAmount() <= 0){
                    output = "REMOVE,ITEM," + idx;
                    inventory.remove(idx);
                }else
                    output = "UPDATE,ITEM," + idx + ",AMOUNT," + inventory.get(idx).getAmount();
                restoreEnergy(1.0f);


            }else
                goal = "find_food";

        }
        else if(makeStatusMessage().contains("interact_other") && goal.equals("none")){
            output = interactWithEntity(step, turnSize, map);
        }
        // - if no goal is set do nothing

        // - also update needs here

        return output;
    }

    // TODO: better energy management
    //  need to add how energy is used mre frequently, I mean living requires energy too and not just moving
    //  - Entities should constantly be consuming energy.
    //  - Depending on it's mutation they will handle this constant loss of energy differently.
    private String energyManagement(int step, int turnSize, LunaMap map, int daySize){
        String output = "";
        if(energy <= 0 && step % turnSize*daySize == 0)
            stats[0]--;

        // just living requires energy, will be based on the entities idle rate.
        decreaseEnergy(step, idleEnergyCostRate);
        // consume food
        if(targetObjectReached() && targetObject.getType() == 1){
            if(!targetInMap(map)){
                releaseTarget();
            }else {
                output = "REMOVE,OBJECT," + targetObject.getGps()[0] + "," + targetObject.getGps()[1] + "," + targetObject.getListId();
                // now, if they have the restraint to not eat all of the food, they will receive a food item
                if(personality.getAmbition() >= .5) // for now using 5
                    output += ",SAVE";
                restoreEnergy(1.0f); // restore 100%% of energy
                goal = "none";
            }
        }

        return output;
    }

    // Determine each need at a given time, called after every update function
    private String needManagement(){
        // need 1) Physiological need
        int h = 0;
        if(makeStatusMessage().contains("hungry"))
            h = 1;
        int z = 0;
        if(makeStatusMessage().contains("sleepy"))
            z = 1;
        int c = 0;
        if(makeStatusMessage().contains("build_camp"))
            c = 1;
        int t = 0;
        if(makeStatusMessage().contains("thirsty"))
            t = 1;
        float physio = (h * .25f) + (z * .25f) + (c * .25f) + (t * .25f);
        // need 2) Safety need - TBD - Depends on actual community structure
        // need 3) Social need
        // need 4) Esteem need
        // need 5) self-actualization need - self development - leveling up, training, other self building activities
        //
        return "";
    }

    // 0 left, 1 right, 2 up, 3 down
    public boolean moveTowardsFood(int step, LunaMap map){
        // use sense bound
        // - if food is found in its sense move to the first one it sees
        // - first find one food object, if found save its location
        // move until the food tile position matches your tile position

        // first get the objects
        if(targetObject == null) {
            targetObject = (WorldObject) getTargetInSense("object_food", map);
            return false;
        }else{
            return moveToTarget(targetObject.getGps(), step);
        }
    }

    private boolean moveToTarget(int[] targetGps, int step){
        if(gps[0]/scale != targetGps[0]/scale &&
                gps[1]/scale != targetGps[1]/scale){
            // find which way we should move
            if(gps[0] < targetGps[0])
                move(3, step); // move down
            if(gps[0] > targetGps[0])
                move(2, step); // move up
            if(gps[1] < targetGps[1])
                move(1, step); // move right
            if(gps[1] > targetGps[1])
                move(0, step); // move left
            return false;
        }else
            return true;

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
            move(this.direction, step);

            this.moves--;
        }else{
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

    public String addItem(Item item){
        System.out.println("Entity(" + getId() + ") Adding item : " + item.toString());
        if(inventory.size() < carryingCapacity){
            int idx = itemTypeInInventory(item.getNamespace());
            if(idx == -1) {
                inventory.put(item.getUniqueID(), item);
                return "added_new_item";
            }else{
                inventory.get(idx).addAmount(item.getAmount());
                return getId() + ",REMOVE,ITEM," + item.getUniqueID();
            }

        }else{
            // see if it can drop anything
        }
        return "fail";
    }

    public int dropItem(int id){
        if(inventory.containsKey(id)){
            inventory.remove(id);
            return id;
        }
        return -1;
    }

    public int destroyItem(int invIdx){
        return dropItem(invIdx);
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
        state.put("GOAL", goal);
        state.put("STATS", stats);
        state.put("PERSONALITY", personality);
        state.put("NEEDS", needs);
        state.put("STATUS", makeStatusMessage());
        state.put("ITEMS", inventory);
        state.put("MutationInfo", new float[]{baseEnergyCost, deathChance, replicationChance});
        //state.put("BONDS", bondList.toArray());
        return state;
    }

    @Override
    public void updateState() {
        // update state, make changes to state variables I guess.
        //hp, maxHp, xp, maxXp, lvl, dmg, speed, sense, energy, maxEnergy, lifeSpanInTurns, TODO: add strength

    }

    @Override
    public String makeStatusMessage(){
        String status = "";
        if(hasLowEnergy())
            status += "hungry,";
        if(hasLowHp())
            status += "hurt,";
        if(isExhausted())
            status += "tired,";
        if(isThirsty())
            status += "thirsty,";
        // TODO: add additional status requests
        if(dailyInteractionCount < dailyInteractionNeed && interactionTimer <= 0)
            status += "interact_other,";
        if(status.length() == 0)
            status = "normal";
        else
            status = status.substring(0, status.length()-1);
        return status;
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
                energy >= maxEnergy*replicationCost && stats[11] > 0);
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

    public boolean isExhausted(){
        return exhaustion >= .65;
    }

    public boolean isThirsty(){
        return thirst >= .65;
    }



    public int getScale(){
        return scale;
    }
    public Entity makeEntity(){
        reduceEnergy(maxEnergy*replicationCost);
        if(Utility.getRnd().nextFloat() < .3)
            return new MutationA(scale, new int[]{gps[0], gps[1], gps[2]}, this.sim_id);
        return new Entity(scale, new int[]{gps[0], gps[1], gps[2]}, this.sim_id);
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
            reduceEnergy(baseEnergyCost * rate);
            stats[8] = (short)(energy);
        }
    }

    // interact with the maps entities
    // - if an entity sees another entity in its sights, depending on its extroversion it will try
    //   to interact with the other entity
    // TODO: finish this guy - need to unit test it
    public String interactWithEntity(int step, int turnSize, LunaMap map){
        // if we don't have a target entity, look for one
        // TODO: The entity should not look for this every time, it should have an interaction need variable, that
        //  changes based the personality the entity has
        if(interactingEntity == -1){
            Object val = getTargetInSense("entity_same", map);
            if(val != null && (Integer)val != -1) interactingEntity = (Integer)val;

        }else{
            // added the try as it can error out if the target entity dies before reaching it
            try {
                int[] targetGps = EntityManager.entities.get(interactingEntity).getGps();

                //System.out.println("Entity " + getId() + " moving to " + interactingEntity);
                if (nextToTarget(targetGps)) {
                    faceTarget(targetGps);
                    //System.out.println("Entity " + getId() + " interacted with Entity " + interactingEntity);
                    // interact here
                    // then set interacting to -1
                    interactingEntity = -1;
                    // modify interacting need so we don't need to interact for a sec
                    interactionTimer = interactionTimerMax;
                    dailyInteractionCount++;
                } else {
                    boolean reached = moveToTarget(targetGps, step);
                    if (reached) {
                        // lock entity
                        String action = interact(EntityManager.entities.get(interactingEntity));
                        // other updates here

                        return action;
                    }
                }
            }catch (IndexOutOfBoundsException ex){
                System.out.println("Entity " + id + " had a target of " + interactingEntity + " which was out of bounds!");
                interactingEntity = -1;
                interactionTimer = interactionTimerMax;
            }catch (NullPointerException ex){
                System.out.println("Entity " + id + " had a target of " + interactingEntity + " which returned a null value!");
                interactingEntity = -1;
                interactionTimer = interactionTimerMax;
            }
        }
        return "";
    }

    // TODO: interacting with entities
    // return an interaction request to send to another entity
    public String interact(Entity e){
        // TODO: compare personalities
        //  - create a send value to pass as a command
        // return an action request
        // - send interaction to the other entity
        return "";
    }

    // TODO: receiving an interaction value - will be called in entity manager where the interaction will be received
    // when an entity receives an interaction, they will interpret the interaction themselves too.
    public float receiveInteraction(float f){
        // TODO: interpret the received interaction value
        //  - can be interpreted correctly or incorrectly based on own personality
        return 0f;
    }


    private Object getTargetInSense(String target, LunaMap map){
        int entityIDCalled = -1;
        try {
            String[] split = target.split("_");
            Rectangle sense = getSenseBound();
            // loop through each tile and check for food in the each tile if
            int kernel = 1;
            int kx = gps[1] / world_scale;
            int ky = gps[0] / world_scale;
            boolean found = false;
            int world_size = map.getObjectsInMap().size();
            // while kernel width is less than sense width
            while (kernel <= sense.width / world_scale) {
                for (int y = 0; y < kernel; y++) {
                    for (int x = 0; x < kernel; x++) {
                        if (((y == 0 || y == kernel - 1) || (x == 0 || x == kernel - 1)) && (ky >= 0 && kx >= 0 && ky + y < world_size && kx + x < world_size)) {
                            if (split[0].equals("object")) {
                                for (WorldObject obj : map.getObjectsInMap().get(ky + y).get(kx + x)) {
                                    if (split[1].equals("food") && obj.getType() == 1) {
                                        return obj;
                                    }
                                }
                            } else if (split[0].equals("entity")) {
                                for (Integer[] id : EntityManager.entityRef.get(gps[2]).get(ky + y).get(kx + x)) {
                                    entityIDCalled = id[0];
                                    if (id[0] != getId() && EntityManager.entities.containsKey(id[0]) && EntityManager.entities.get(id[0]).getType() < 5
                                            && EntityManager.entities.get(id[0]).getGps()[2] == getGps()[2]) // also see if they are in the same map
                                        return id[0];
                                }
                            }
                        }
                    }
                }
                kernel += 2;
                kx--;
                ky--;
            }
            if (split[0].equals("entity"))
                return -1;
        }catch (Exception ex){
            System.out.println("error in getTargetSense, called by Entity " + id + ":");
            System.out.println("EntityID called? " + entityIDCalled + " entity map values: " + Utility.makeArrString(EntityManager.entities.keySet().toArray()));
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    // take any kind of target, entity or object and check if its next
    private boolean nextToTarget(int[] targetGps){
        if(targetGps.length <= 2) {
            System.out.println("Entity " + id + " called nextToTarget with an array not correct size -> " + targetGps.length);
            return false;
        }
        Rectangle adjacentBound = getAdjacentBound();
        return adjacentBound.contains(targetGps[1], targetGps[0]);
    }

    private Rectangle getAdjacentBound(){
        return new Rectangle(gps[1]-world_scale, gps[0]-world_scale, world_scale*3, world_scale*3);
    }

    private void faceTarget(int[] targetGps){
        if(targetGps[0] > gps[0]) // look down
            direction = 3;
        else if(targetGps[0] < gps[0]) // look up
            direction = 2;
        else if(targetGps[1] > gps[1]) // look right
            direction = 1;
        else
            direction = 0; // else look left
    }

    // good for facing entities
    public int faceOpposite(int dir){
        switch(dir){
            case 0: // left
                return 1;
            case 1:// right
                return 0;
            case 2:// up
                return 3;
            case 3:// down
                return 2;
            default:
                return 0;
        }
    }//

    private void updateLock(){
        if(locked > 0)
            locked--;
    }
    private void updateLock(int lock){
        locked = lock;
    }

    public boolean locked(){
        return locked > 0;
    }

    private int itemTypeInInventory(String type){
        for(int key : inventory.keySet()){
            if(inventory.get(key).getNamespace().contains(type))
                return key;
        }
        return -1;
    }

    public void shutdown(){
        this.entityLog.closeReport();
    }


    public String makeEntityReportLine(){
        String out = "";
        // gps, personality, stats, goal, status message, needs, inventory, mutation info
        out += "\"gps\":"+ Utility.makeArrString(gps) + ",";
        out += "\"personality\":" + personality.toString() + ",";
        out += "\"stats\":" + Utility.makeArrString(stats) + ",";
        out += "\"goal\":" + goal + ",";
        out += "\"status\":" + makeStatusMessage() + ",";
        out += "\"needs\":" + Utility.makeArrString(needs) + ",";
        out += "\"inventory\":" + Utility.makeArrString(inventory.keySet().toArray()) + ",";
        out += "\"mutation_info\":" + Utility.makeArrString(new float[]{baseEnergyCost, deathChance, replicationChance});
        return out;
    }

    public int getSimId() {
        return sim_id;
    }

    private void reduceEnergy(float f){
        energy -= f;
        if(energy < 0)
            energy = 0;
    }

    public void subReplicate(){
        stats[11] -= 1;
        if(stats[11] < 0)
            stats[11] = 0;
    }
}

