package luna.entity;

import luna.entity.util.*;
import luna.main.Game;
import luna.util.Animation;
import luna.util.Logger;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;
import luna.world.util.ObjectManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Entity implements Actions{
    // position stuff
    protected int x, y, lastX, lastY, world_w, world_h;
    protected int waitTime = 0, maxWaitTime = 70; // needs to be greater than ticks per second
    protected int interactTimer = 0;
    protected int huntWaitTime = 0; // to allow entities that just spawn to live for at lease some time
    protected int attackTimer = 0, attackWaitTime = 35, hungerWaitTime = 0;
    protected int dmgTimer = 0, dmgWaitTime = 5; // less because entities can attack quickly
    private int entityID;
    //
    private PriorityQueue<TaskRef> taskQueue;
    private int taskWaitTimer = 0;
    //
    protected int type = 0; // 0 is the base entity
    // sub positioning
    // - When an entity is on a tile and engages in an activity on said tile,
    //   they will be placed in a sub map of that tile.
    protected int subX, subY, lastSubX, lastSubY, subTileX, subTileY;
    protected int position = -1; // -1 being overworld, >= 0 being subMap. others can be used later to represent key maps that become static.


    // entity specific stuff
    // - these guys will help explain what an entity is/what makes it unique
    protected int size; // size of bound
    protected int sub_scale;
    protected int hp, max_hp, xp, max_xp, drop_xp, dmg, level;
    protected int hunger, max_hunger, hunger_loss_rate;
    private static Color entity_base_color = new Color(255, 102, 153);

    private static Color hp_color = new Color(255, 16, 38, 160);
    private static Color hungerColor = new Color(255, 130, 14, 182);
    private static Color shadow = new Color(0,0,0,130);
    // Map of spriteSheets
    protected Map<String, BufferedImage[]> spriteSheetMap = new HashMap<String, BufferedImage[]>();

    // our map of animations
    protected Map<String, Animation> animationMap = new HashMap<>();
    // our current frame
    protected String currentAnimation = "Down";

    // moving variables
    private int moves = 0, move_wait = 10, max_moves = 25;
    private int velocity = 2;
    private Point target_point = new Point(-1,-1);
    protected int direction = 0, world_scale = 1;
    protected int lastPosition = -1;

    protected int currTileX, currTileY;
    protected int numCollisions = 0, collisionTimer = 0;

    protected int targetEntityID = -1;
    protected boolean targetAdjacent = false;
    protected List<InteractableObject> objectsOnPerson = new ArrayList<>();

    private Map<String, List<Integer>> savedLocations;

    protected List<List<Integer>> movesLeft = new ArrayList<>();
    // this will be called by the constructors to give our guys their base stat
    //  values.

    protected String focus; // what this entity focuses on, like fighting, crafting, cooking.
                            // Also, that does not mean an entity can only do one thing, its just
                            // that alon it may only do what it's focus is. but if it has a group
                            // it will be able to do more.
                            // A focus can also change overtime, especially when they level up.
                            // More on this when there are actual focus areas build in the game.
    protected List<Bond> bondList;
    protected Logger logger;
    protected Logger taskLogger;
    protected Logger positionLogger;
    private static TaskRef blankRef = new TaskRef();
    protected static EntityUtil eUtil = new EntityUtil();
    // TODO: personality changes
    //  [_] Adding actions based on personality
    //  [_] Interacting with other entities
    //  [_] Adding focus def

    // all a scale of 0 to 10
    protected int creativity;
    protected int conscientiousness;
    protected int extroversion;
    protected int agreeableness;
    protected int neuroticism;
    protected boolean locked = false; // maybe rename this
    protected int lockCounter = 0;
    protected int emoteSize;
    protected int groupId = -1;
    private boolean init = false;
    private int idleTime = 0;

    private int visionKernel;

    private static int counter = 0;
    // TODO:
    //  [_] Adding diet preferences (based on entity type)
    //  [_] Add attacking sprites

    public void set_stats(){
        this.size = world_scale;
        this.sub_scale = Game.sub_world_scale;
        this.emoteSize = (int)(size/4);
        this.logger = new Logger("./logs/EntityLogs/entity_" + this.entityID + ".txt");
        this.taskLogger = new Logger("./logs/taskLogs/entity_" + this.entityID + "_TaskLog.txt");
        this.positionLogger = new Logger("./logs/positionLogs/Entity_" + this.entityID + "_posLog.txt");
        logger.write("init stats");
        this.max_hp = (int)(Math.random() * 3) + 3;
        this.dmg = (int)(Math.random() * 3) + 1;
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

        this.creativity = 5;
        this.conscientiousness = 5;
        this.extroversion = 5;
        this.agreeableness = 5;
        this.neuroticism = 5;

        focus = "nomad"; // TODO: assign focus based on traits

        logger.write("Making entity...");
        logger.writeNoTimestamp("Entity " + this.entityID);
        logger.writeNoTimestamp("hp:    " + this.max_hp);
        logger.writeNoTimestamp("dmg:   " + this.dmg);
        logger.writeNoTimestamp("focus: " + focus);
        logger.writeNoTimestamp("pos: [" + this.currTileY + " " + this.currTileY + "]");

    }

    private void baseInit(){
        this.bondList = new ArrayList<>();
        this.taskQueue = new PriorityQueue<>();
        this.savedLocations = new HashMap<>();
        this.savedLocations.put("food", new ArrayList<>());
        this.savedLocations.put("hostile", new ArrayList<>());
        //
        this.visionKernel = 3; // others can have large vision sight
        set_stats();
        makeImages();
        logger.write("created entity # " + counter + " with entity ID of " + getEntityID());
        counter++;
        World.editRefMap("add", position, entityID);
    }

    // some other setups
    public void makeImages(){
    	// These are just the idle images/ moving images
    	// - other frames will be added later
        logger.write("Init images");
        String leftPath = "res/Left_slime_bob.png";
        String rightPath = "res/Right_slime_bob.png";
        String upPath = "res/Up_slime_bob.png";
        String DownPath = "res/Down_slime_bob.png";
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

        //System.out.println(spriteSheetMap.keySet());
        //System.out.println(animationMap.keySet());
        //
        // now make the direction maps
        // alright good
    }

    //
    public Entity(int world_w, int world_h, int id){
        this.x = 5;
        this.y = 5;
        this.world_h = world_h;
        this.world_w = world_w;
        this.world_scale = 4;
        this.entityID = id;
        baseInit();
    }//
    public Entity(int x, int y, int world_w, int world_h, int world_scale, int id){
        this.x = x;
        this.y = y;
        this.world_h = world_h;
        this.world_w = world_w;
        this.world_scale = world_scale;
        this.entityID = id;
        baseInit();
    }//

    public Entity(int x, int y, int world_w, int world_h, int world_scale, Color c, int id){
        this.x = x;
        this.y = y;
        this.world_h = world_h;
        this.world_w = world_w;
        this.entity_base_color = c;
        this.world_scale = world_scale;
        this.entityID = id;
        baseInit();
    }//

    // draw our guy
    public void render(Graphics2D g){
        if (position == -1 && (groupId == -1 || isGroupLeader())) {
            g.setColor(entity_base_color);
            //g.fillRect(this.x,this.y,this.size*world_scale,this.size);
            drawHpBar(g, x, y);
            animationMap.get(currentAnimation).drawAnimation(g, this.x, this.y, size, size);
            if(locked)animationMap.get("Talking").drawAnimation(g,this.x+size,this.y,emoteSize,emoteSize);
            g.setColor(Color.black);

        }
        else if(position != -1){
            //first lets draw the sub tile map off to the side of the screen
            animationMap.get(currentAnimation).drawAnimation(g, World.subMaps.get(position).getRenderXStart()+this.subX,
                                                                World.subMaps.get(position).getRenderYStart()+this.subY,
                                                                   sub_scale, sub_scale);
            drawHpBar(g,World.subMaps.get(position).getRenderXStart()+this.subX, World.subMaps.get(position).getRenderYStart()+this.subY);
            if(locked)animationMap.get("Talking").drawAnimation(g,World.subMaps.get(position).getRenderXStart()+this.subX+sub_scale,
                                                                                    World.subMaps.get(position).getRenderYStart()+this.subY,
                                                                                       emoteSize/2, emoteSize/2);
        }//
    }//

    // move here
    public void update(List<List<Tile>> tileMap, int seconds){
        if(!init){
            TaskRef wander = new TaskRef(getEntityID(), 4, new int[]{getCurrTileY(), getCurrTileY(), getPosition()}, tileMap, seconds);
            taskQueue.add(wander); // the wander task will never be removed just pushed to the back
            init = true;
        }// end of init
        if(position != -1){
            if(lastSubX == subX && lastSubY == subY)
                idleTime++;
            else
                idleTime = 0;
            lastSubX = subX;
            lastSubY = subY;
        }else{
            if(lastX == x && lastY == y)
                idleTime++;
            else
                idleTime = 0;
            lastX = x;
            lastY = y;
        }
        animationMap.get(currentAnimation).runAnimation();
        if(locked) animationMap.get("Talking").runAnimation();

        taskManagement(tileMap, seconds);
        if(!locked)
            moveManagement(seconds);
        if(targetEntityID != -1)
            attack(targetEntityID);

        /* Comment these lines to turn off entity grouping/discovery function */
        //if(interactTimer <= 0 && (getCurrentTask().getTaskType().equals("wander") || getCurrentTask().getTaskType().equals("none")))
        //    survey(seconds);

        hungerManagement(seconds);

        // animation calls
        currentAnimation = Util.intToStringDirectionMap.get(direction);
        //

        // there can be an issue with collisions when transitioning from a sub map to the overworld
        if(collisionTimer == 0)
            collisionManagement(tileMap);
        // other managements here
        if(waitTime > 0)
            waitTime--;
        if(collisionTimer > 0)
            collisionTimer--;
        if(attackTimer > 0)
            attackTimer--;
        if(dmgTimer > 0)
            dmgTimer--;
        if(hungerWaitTime > 0)
            hungerWaitTime--;
        if(interactTimer > 0)
            interactTimer--;
        if(huntWaitTime > 0)
            huntWaitTime--;
        if(taskWaitTimer > 0)
            taskWaitTimer--;
        if(lockCounter > 0){
            lockCounter--;
            if(lockCounter <= 0)
                interactTimer = maxWaitTime;
        }
        else
            locked = false;
        if(groupId != -1){
            World.entityManager.groups.get(groupId).checkEntityStatus();
            if(isGroupLeader()){
                if(World.entityManager.groups.get(groupId).size() < 1){
                    // dispand group
                    World.entityManager.groups.remove(groupId);
                    logger.write("Group " + groupId + " disbanded");
                    groupId = -1;
                }
            }
        }

        if(position == -1)
            positionLogger.write("y,x,pos|" + getX() + "," + getY() + "," + getPosition());
        else
            positionLogger.write("y,x,pos|" + getSubX() + "," + getSubY() + "," + getPosition());
    }///

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

    public int getMoves() {
        return moves;
    }

    // moving in sub maps
    public void subMapMovement(){
        if (taskQueue.isEmpty() || (getCurrentTask().getTaskType().equals("none")|| getCurrentTask().getTaskType().equals("wander") || getCurrentTask().getTaskType().equals("hostile"))) {
            //if(type == 0) System.out.println("sub map movement");
            // If we do not have a goal wander
            if(targetEntityID == -1) wander();
            setSubMapTarget();
            moveToTarget();
            // lets check if the encounter we are in is active
            /*
            try { // many checks added
                if (getCurrentTask().getTargetGPS()[3] != -1 && World.tileMap.get(currTileY).get(currTileX).getObjectsInTile().size() > 0 &&
                        !World.tileMap.get(currTileY).get(currTileX).getObjectsInTile().get(getCurrentTask().getTargetGPS()[3]).isActive()) {
                    if(groupId == -1) changePosition(-1);
                    else if(groupId != -1 && isGroupLeader())
                        World.entityManager.groups.get(groupId).groupChangePosition(-1);
                }
            }catch (Exception ex){
                System.out.println(ex.getMessage());
                System.out.println(ex.getCause());
                System.out.println("Current Task objectID -> " + getCurrentTask().getTargetGPS()[3]);
                System.out.println("current Tile -> [" + currTileY + " , " + currTileX + "]");
                System.out.println("number of object at tile -> " + World.tileMap.get(currTileY).get(currTileX).getObjectsInTile());
                System.exit(1);
            }*/
        } else {
            // move to target
            executeMoves();
        }
        // first look for other entities
    }

    // use a switch to decide which direction to move
    @Override
    public void move(int direction){
        //System.out.println(direction);
        switch (direction){
            case 0:{
                // move left
                if(this.position == -1)
                    this.x -= this.velocity;
                else
                    this.subX -= this.velocity;
                break;
            }
            case 1:{
                // move right
                if(this.position == -1)
                    this.x += this.velocity;
                else
                    this.subX += this.velocity;
                break;
            }
            case 2:{
                // move up
                if(this.position == -1)
                    this.y -= this.velocity;
                else
                    this.subY -= this.velocity;
                break;
            }
            case 3:{
                // move down
                if(this.position == -1)
                    this.y += this.velocity;
                else
                    this.subY += this.velocity;
                break;
            }
            default:
                break;
        }// end of switch
    }// end of move

    // return true if we go out of bounds
    public boolean collision(){
        if(this.x <= 0 || this.y <= 0 || this.x >= this.world_w || this.y >= this.world_h) {
            return true;
        }
        return false;
    }// end of collision
    public boolean collision(List<List<Tile>> tileMap){
        if(this.subX <= 0 || this.subY <= 0 || this.subX >= (tileMap.get(0).size())*Game.sub_world_scale || this.subY >= (tileMap.size())*Game.sub_world_scale){
            return true;
        }
        return false;
    }// end of collision

    // other helpers

    // A helper to draw the hp bar based on the current health
    public void drawHpBar(Graphics2D g, int x, int y){
        g.setColor(hp_color);
        int barWidth, hungerBarWidth;
        if(position == -1) {
            barWidth = (size * this.hp) / this.max_hp;
            hungerBarWidth = (size * this.hunger) / this.max_hunger;
        }else{
            barWidth = (sub_scale * this.hp) / this.max_hp;
            hungerBarWidth = (sub_scale * this.hunger) / this.max_hunger;
        }
        g.fillRect(x-1,y-5, barWidth, 2);
        g.setColor(hungerColor);
        g.fillRect(x-1,y-3, hungerBarWidth, 2);
        g.setColor(Color.black);
        g.drawString("" + getEntityID(), x-1, y-8);
        g.setColor(this.shadow);

        if(position == -1 && isGroupLeader()){
            // draw the members of your group above the hp bar
            g.setColor(Color.cyan);
            for(int i = 0; i < World.entityManager.groups.get(groupId).size()-1; i++){
                animationMap.get(currentAnimation).drawAnimation(g, (this.x) + (((int)(size/3) + 3) * i), this.y-5, (int)(size/3), (int)(size/3));
            }
        }
    }

    // a bound helper
    public Rectangle getBounds(){
        return new Rectangle(this.x,this.y,this.size,this.size);
    }// end of get bounds

    // this guy returns a rectangle of the contact bounds
    // so if this contact bound intersects with anothers contact bound
    //  they could then choose to interact with one another.
    public Rectangle getContactBounds(){
        return new Rectangle(this.x-this.size*2, this.y-this.size*2, (this.size*2)*3,(this.size*2)*3);
    }

    //
    public void executeMoves(){
    	// changed from move to point to execute moves
        // - will execute the moves available in the current task
        if(getCurrentTask().getMoves().size() > 0 && position == -1){
            // else we don't need to do anything
            if(currTileY != getCurrentTask().getMoves().get(0).get(0)){
                // move vert
                if(currTileY > getCurrentTask().getMoves().get(0).get(0))
                    move(2);
                else if(currTileY < getCurrentTask().getMoves().get(0).get(0))
                    move(3);
            }
            if(currTileX != getCurrentTask().getMoves().get(0).get(1)){
                // move herz
                if(currTileX > getCurrentTask().getMoves().get(0).get(1))
                    move(0);
                else if(currTileX < getCurrentTask().getMoves().get(0).get(1))
                    move(1);
            }
            // if we are at the next move now, we need to remove it;
            if(currTileY == getCurrentTask().getMoves().get(0).get(0) && currTileX == getCurrentTask().getMoves().get(0).get(1)){
                getCurrentTask().getMoves().remove(0);
            }
        }else if(getCurrentTask().getMoves().size() > 0){
            // else we don't need to do anything
            if(subTileY != getCurrentTask().getMoves().get(0).get(0)){
                // move vert
                if(subTileY > getCurrentTask().getMoves().get(0).get(0))
                    move(2);
                else if(subTileY < getCurrentTask().getMoves().get(0).get(0))
                    move(3);
            }
            if(subTileX != getCurrentTask().getMoves().get(0).get(1)){
                // move herz
                if(subTileX > getCurrentTask().getMoves().get(0).get(1))
                    move(0);
                else if(subTileX < getCurrentTask().getMoves().get(0).get(1))
                    move(1);
            }
            // if we are at the next move now, we need to remove it;
            if(subTileY == getCurrentTask().getMoves().get(0).get(0) && subTileX == getCurrentTask().getMoves().get(0).get(1)){
                getCurrentTask().getMoves().remove(0);
            }
        }
    }

    // a collision between other entities

    // basic compareTO, later I will make a detailed compareTo that compares stats and traits
    public boolean compareTo(Entity e){
    	if(getType() == e.getType()) return true;
    	else return false;
    }

    //
    // For log reporting
    // after every iteration an entity will have a status based on health, hunger, happiness etc.
    public String makeStatusMessage(){
        String taskOut = getTaskNeed();
        if(taskOut.length() > 0) return taskOut;
        return "I'm Oaky";
    }

	@Override
	public void attack(int entityID) {
		if(attackTimer == 0 && targetAdjacent && hp > 0){
		    logger.write("Attacking entity [" + entityID + "], a " + EntityManager.entities.get(entityID).getType());
		    //System.out.println("[" + getEntityID() + "] Attack [" + entityID + "]");
		    attackTimer = attackWaitTime;
		    try{
		        if(EntityManager.entities.get(entityID).isAlive() && EntityManager.entities.get(entityID).getPosition() == getPosition())
                    EntityManager.entities.get(entityID).takeDmg(getDmg(), this.getEntityID());
		        else if(!EntityManager.entities.get(entityID).isAlive()){
		            addXp(EntityManager.entities.get(entityID).getDrop_xp());
		            targetEntityID = -1;
                }
            }catch (Exception ex){
		        System.out.println("Error accessing entity " + entityID);
		        System.out.println("Called from attack in entity " + getEntityID());
		        System.out.println("Size of entity list -> " + EntityManager.entities.size());
		        targetEntityID = -1;
            }
        }//
	}// end of attack

    @Override
	public void eat(InteractableObject e) {
        //String tmp[] = e.getType().split("_");
        logger.write("Ate a " + e.getType());
		this.hunger = this.max_hunger;
		heal((int)(this.max_hp*.1));
	}

	@Override
    public InteractableObject dropObject(int objPos){
        InteractableObject obj = this.objectsOnPerson.get(objPos);
        this.objectsOnPerson.remove(objPos);
        return obj;
    }

    public int getEntityID() {
        return entityID;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
    }

    public Logger getTaskLogger() {
        return taskLogger;
    }

    // shutdown everything in the queue
    public void shutdown(){
        while(!taskQueue.isEmpty()){
            TaskRef ref = taskQueue.poll();
            System.out.println(ref.getTaskType() + " | " + ref.getNotes());
        }
        this.logger.closeWriter();
        this.taskLogger.closeWriter();
        this.positionLogger.closeWriter();
    }

    public String toString(){
        return "Entity :: " + this.entityID +
             "\nHP     :: " + this.max_hp +
             "\nLevel  :: " + this.level +
             "\nHunger :: " + this.hunger +
             "\nMapPos :: " + this.position +
             "\n";
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        changePosition(position); // we want to be sure that the correct things are called when setting this
    }


    // manage movement (All kinds)
    protected void moveManagement(int seconds){
        // MOVEMENT
        // moving in sub maps
        if(position != -1){
            subMapMovement();
        }else {
            // only leaders or independent can manage movement
            if(groupId == -1 || isGroupLeader()) {
                // moving on the overworld
                // move based on task set
                if (!taskQueue.isEmpty() && (getCurrentTask().getGoal() == 0 || getCurrentTask().getGoal() == 4 || getCurrentTask().getTaskType().equals("interact"))) {
                    // If we do not have a goal wander
                    if(!isLocked())wander();
                } else if(!taskQueue.isEmpty()) {
                    // move to target
                    executeMoves();
                }
            }else if((groupId != -1 && !isGroupLeader()) && this.position == -1){
                validGroupID();
                int leaderID = World.entityManager.groups.get(groupId).getLeader();
                x = EntityManager.entities.get(leaderID).getX();
                y = EntityManager.entities.get(leaderID).getY();
            }

        }
    }// END OF MOVEMENT

    private void hungerManagement(int seconds){
        // hunger management
        if(seconds > 0 && seconds % 5 == 0 && hungerWaitTime <= 0){
            if(this.hunger > 0){
                this.hunger -= this.hunger_loss_rate;
                hungerWaitTime = maxWaitTime;
            }else{
                takeDmg((int)(.05*this.max_hp) + 1, -1);
                hungerWaitTime = maxWaitTime;
            }

        }
    }// end of hunger management

    // collision module
    private void collisionManagement(List<List<Tile>> tileMap){
        /*collision and location management*/
        if(position == -1){
            if (collision()) {
                x = lastX;
                y = lastY;
            }
            int tileX = (x / world_scale);
            int tileY = (y / world_scale);
            if(tileX < 0) tileX = 0;
            if(tileY < 0) tileY = 0;
            //
            if (tileX != currTileX || tileY != currTileY) {
                try { // Bounds error handling
                    if (tileY >= tileMap.size())
                        tileY = tileMap.size() - 1;
                    if (tileX >= tileMap.size())
                        tileX = tileMap.get(0).size() - 1;
                    tileMap.get(currTileY).get(currTileX).removeEntity(this.entityID);
                    tileMap.get(tileY).get(tileX).addEntity(this);
                    currTileX = tileX;
                    currTileY = tileY;
                } catch (Exception ex) {
                    // If something happens I need to see if it was a bounding issue
                    System.out.println("Error occurred with entity " + this.entityID);
                    System.out.println(ex.getMessage());
                    System.out.println(currTileY + " " + currTileX);
                    System.out.println(tileY + " " + tileX);
                    System.out.println("Tile map info:");
                    System.out.println(tileMap.size());
                    System.out.println(tileMap.get(0).size());
                    System.exit(-1);
                }
                //System.out.println("tileMapPos = [" + currTileY + "][" + currTileX + "]");
            }
        }
        else{
            // sub positioning collisions
            int tempWorldSize = World.subMaps.get(position).getHeight()/world_scale;
            if (collision(World.subMaps.get(position).getTileMap())) {
                subX = lastSubX;
                subY = lastSubY;
                if (collision(World.subMaps.get(position).getTileMap())){
                    subX = 5;
                    subY = 5; // just reset it
                }
                //System.out.println("Sub collision");
            }
            int tileX = (subX / Game.sub_world_scale);
            int tileY = (subY / Game.sub_world_scale);
            //
            if (tileX != subTileX || tileY != subTileY) {
                try { // Bounds error handling
                    if (tileY >= tempWorldSize-1)
                        tileY = tempWorldSize - 2;
                    if (tileX >= tempWorldSize-1)
                        tileX = tempWorldSize - 2;
                    //System.out.println("--- collision ---");
                    //System.out.println(tempWorldSize);
                    //System.out.println(tileX + " " + tileY);
                    //System.out.println(currentTask.targetMapPos);
                    World.subMaps.get(position).getTileMap().get(subTileY).get(subTileX).removeEntity(this.entityID);
                    World.subMaps.get(position).getTileMap().get(tileY).get(tileX).addEntity(this);
                    subTileX = tileX;
                    subTileY = tileY;
                } catch (Exception ex) {
                    // If something happens I need to see if it was a bounding issue
                    System.out.println("Sub Map Error occurred with entity " + this.entityID);
                    System.out.println(ex.getMessage());
                    System.out.println(subTileY + " " + subTileX);
                    System.out.println(tileY + " " + tileX);
                    System.out.println("Tile map info:");
                    System.out.println(tileMap.size());
                    System.out.println(tileMap.get(0).size());
                    System.exit(-1);
                }//*/
                //System.out.println("tileMapPos = [" + currTileY + "][" + currTileX + "]");
            }
        }
    }

    public void setSubMapTarget(){
        // Needs to survey the are and see if any entities that are targets of this entity
        if(position != -1){
            if(targetEntityID != -1){
                // check if the entity is alive still
                if(EntityManager.entities.get(targetEntityID).getPosition() != getPosition() ||
                        !EntityManager.entities.get(targetEntityID).isAlive()){
                    if(!EntityManager.entities.get(targetEntityID).isAlive())
                        logger.write("Defeated entity " + targetEntityID + " a " + EntityManager.entities.get(targetEntityID).getType());
                    targetEntityID = -1; // unlock our target
                }

            }else if(targetEntityID == -1){
                int kernel = 3, kx, ky;
                int mapSize = World.subMaps.get(position).getTileMap().size()-1;
                for(int y = -1; y < kernel-1; y++){
                    ky = subTileY + y;
                    for(int x = -1; x < kernel-1; x++){
                        kx = subTileX + x;
                        if(ky >= 0 && kx >= 0 && ky <= mapSize && kx <= mapSize){
                            for(int id : World.subMaps.get(position).getTileMap().get(ky).get(kx).getEntitiesInTile()){
                                if(EntityManager.entities.get(id).isAlive() && id != this.getEntityID() &&
                                        getType() != EntityManager.entities.get(id).getType() &&
                                        EntityManager.entities.get(id).getPosition() == getPosition()){
                                    targetEntityID = id;
                                    //System.out.println("Target Set -> \n" + World.entities.get(targetEntityID).toString());
                                    //System.exit(1);
                                    logger.write("Targeting entity " + id + " a " + EntityManager.entities.get(id).getType());
                                    return; // just leave the function
                                }//
                            }//
                        }//
                    }//
                }//
            }// end of assignment
        }
    }// end of set sub map target

    // will move to a target
    //  will move to an adjacent tile of the target (all cardinal directions)
    public void moveToTarget(){
        int movesMade = 0;
        if(targetEntityID != -1 && EntityManager.entities.get(targetEntityID).isAlive()){
            if(EntityManager.entities.get(targetEntityID).getSubX() < getSubX()-world_scale){
                //System.out.println("Moving Left to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("left"));
                movesMade++;
            }
            if(EntityManager.entities.get(targetEntityID).getSubX() > getSubX()+world_scale){
                //System.out.println("Moving Right to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("right"));
                movesMade++;
            }
            if(EntityManager.entities.get(targetEntityID).getSubY() < getSubY()-world_scale){
                //System.out.println("Moving Up to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("up"));
                movesMade++;
            }
            if(EntityManager.entities.get(targetEntityID).getSubY() > getSubY()+world_scale){
                //System.out.println("Moving Down to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("down"));
                movesMade++;
            }
            targetAdjacent = movesMade == 0;
        }else if(targetEntityID != -1 && !EntityManager.entities.get(targetEntityID).isAlive()){
            targetEntityID = -1;
        }
    }

    public int getSubX() {
        return subX;
    }

    public void setSubX(int subX) {
        this.subX = subX;
    }

    public int getSubY() {
        return subY;
    }

    public void setSubY(int subY) {
        this.subY = subY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDrop_xp() {
        return drop_xp;
    }

    public void setDrop_xp(int drop_xp) {
        this.drop_xp = drop_xp;
    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public int getSubTileX() {
        return subTileX;
    }

    public void setSubTileX(int subTileX) {
        this.subTileX = subTileX;
    }

    public int getSubTileY() {
        return subTileY;
    }

    public void setSubTileY(int subTileY) {
        this.subTileY = subTileY;
    }

    public int getCurrTileX() {
        return currTileX;
    }

    public void setCurrTileX(int currTileX) {
        this.currTileX = currTileX;
    }

    public int getCurrTileY() {
        return currTileY;
    }

    public void setCurrTileY(int currTileY) {
        this.currTileY = currTileY;
    }

    // take damage, usually called by another entity
    public void takeDmg(int dmg, int entityID){
        if(dmgTimer == 0){
            dmgTimer = dmgWaitTime;
            hp -= dmg;
            if(hp <= 0){
                hp = 0;
                logger.write("Entity [" + getEntityID() + "] Died!");
                if(entityID > -1){
                    logger.writeNoTimestamp("Killed by a " + EntityManager.entities.get(entityID).getType() + "!");
                }else if(isHungry()){
                    logger.writeNoTimestamp("Starved to death!");
                }
                logger.writeNoTimestamp(toString());
                World.editRefMap("remove", getPosition(), getEntityID());
                // dead
                if(groupId != -1)
                    World.entityManager.groups.get(groupId).checkEntityStatus();
            }
        }

    }//

    public int getHp() {
        return hp;
    }

    public int getMax_hp(){return this.max_hp;}

    public boolean notWaiting(){return waitTime <= 0;}

    public boolean notWaitingForHunt(){return waitTime <= 0 && huntWaitTime <= 0;}

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getTargetEntityID() {
        return targetEntityID;
    }

    // example, if an enemy has an agr stat to draw attackers
    public void setTargetEntityID(int targetEntityID) {
        this.targetEntityID = targetEntityID;
    }

    public boolean isAlive(){return hp > 0;}

    public void changePosition(int pos){
        int target = pos;
        if(pos == lastPosition) {
            pos = -1; // if it messes up go back to the overworld
            //System.out.println("Trying to set pos to last position -> " + lastPosition);
        }
        if(collisionTimer == 0) { // prevent calling the same thing
            World.editRefMap("remove", getPosition(), getEntityID());
            // changing
            if(pos == -1)
                logger.write("Heading to the overworld ");
            else
                logger.write("Moving to sub map " + pos);
            //if(getCurrentTask() != null)System.out.println("current Goal -> " + getCurrentTask().getTaskType());
            if(pos != -1){
                setSubX(5);
                setSubY(5);
            }
            lastPosition = position;
            position = pos;
            collisionTimer = 1;
            //logger.writeNoTimestamp("Was position change successful? " + (target == getPosition()));
            World.editRefMap("add", getPosition(), getEntityID());
            //if(type == 0 && position != -1)
            //    World.visibleMap = position;
        }
    }

    // heal by amount
    public void heal(int amt){
        this.hp += amt;
        if(this.hp > this.max_hp)
            this.hp = max_hp;
    }

    public void addXp(int x){
        this.xp += x;
        if(this.xp >= max_xp){
            level++;
            this.xp = this.xp-max_xp;
            max_xp = max_xp + (int) (.1 * max_xp) + Util.random(5) + 5; // add 10%
            max_hp = max_hp + (int) (.1 * max_hp) + Util.random(2);
            if(level % 2 == 0) dmg = dmg + (int) (.15 * dmg) + Util.random(2);
            drop_xp = drop_xp + (int) (.25 * drop_xp);

            if(this.getFocus().contains("nomad") && this.level > 5){
                if(this.dmg > 10){
                    this.focus = EntityUtil.getJobs()[2];
                }else{
                    this.focus = EntityUtil.getJobs()[3];
                }
            }
            logger.write("Level Up! ------");
            logger.writeNoTimestamp("level : [" + (level-1) + "] -> [" + level + "]");
            logger.writeNoTimestamp("HP    : " + max_hp);
            logger.writeNoTimestamp("DMG   : " + dmg);
            logger.writeNoTimestamp("XP    : (" + xp + "/" + max_xp + ")");
            logger.writeNoTimestamp("Focus : " + getFocus());
            logger.writeNoTimestamp("-----------------------");
        }
    }

    public String getFocus() {
        return focus;
    }

    public List<Bond> getBondList() {
        return bondList;
    }

    // returns true if entity added to bond list
    public boolean addBond(int entityID){
        if(inBondList(entityID) == -1){
            this.bondList.add(new Bond(entityID));
            return true;
        }
        return false;
    }

    public int getCreativity() {
        return creativity;
    }

    public int getConscientiousness() {
        return conscientiousness;
    }

    public int getExtroversion() {
        return extroversion;
    }

    public int getAgreeableness() {
        return agreeableness;
    }

    public int getNeuroticism() {
        return neuroticism;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        logger.writeNoTimestamp("Setting group id to -> " + groupId);
        this.groupId = groupId;
    }

    public int getInteractTimer() {
        return interactTimer;
    }

    public void setInteractTimer(int interactTimer) {
        this.interactTimer = interactTimer;
    }

    public int getVisionKernel() {
        return visionKernel;
    }

    public void setVisionKernel(int visionKernel) {
        this.visionKernel = visionKernel;
    }

    public TaskRef getCurrentTask(){return this.taskQueue.peek();}

    public boolean isHungry(){return this.hunger < this.max_hunger*.45;}
    // compare the public personality traits of an entity to see if they are compatible to work together
    // - based only on agreeableness and extroversion
    // - later there will be complements, certain stats complement others
    public boolean isCompatible(Entity e){
        if(compareTo(e))
            return (Math.abs(getAgreeableness()-e.getAgreeableness()) < 5 && Math.abs(getExtroversion()-e.getExtroversion()) < 5);
        return false;
    }// end

    public int inBondList(int id){
        for(int i = 0; i < getBondList().size(); i++){
            if(bondList.get(i).getEntityID() == id)
                return i;
        }
        return -1;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getTaskWaitTimer() {
        return taskWaitTimer;
    }

    // face the opposite direction of th e input direction
    public void faceOpposite(int dir){
        direction = eUtil.faceOpposite(dir);
    }//

    public void setLock(int dir){
        locked = true;
        lockCounter = 60 * 3;// about three seconds
        // now face the opposite direction
        faceOpposite(dir);
    }// done


    // This is for interactions, it tells the entity to stop moving and face one another
    // - it'll last for about 3 seconds
    // - this only locks no additional operations
    public void lockEntity(int id){
        int targetDirection = EntityManager.entities.get(id).getDirection();
        setLock(targetDirection);
        EntityManager.entities.get(id).setLock(getDirection());
    }//

    // TODO: define actual logic
    public void entityInteraction(int id){
        // calculate if a positive interaction based on bond and personality traits
        if(inBondList(id) != -1){
            // update both this entity and the target
            int targetBondIdx = EntityManager.entities.get(id).inBondList(this.getEntityID());
            this.getBondList().get(inBondList(id)).updateBond(10); // For now
            setInteractTimer(maxWaitTime*2);
            if(targetBondIdx != -1) {
                EntityManager.entities.get(id).getBondList().get(targetBondIdx).updateBond(10);
                EntityManager.entities.get(id).setInteractTimer(maxWaitTime*2);
            }
        }
    }

    // make task
    public String getTaskNeed(){
        return eUtil.getTaskRequest(this);
    }

    public void setFocus(String s){
        this.focus = s;
    }

    public Map<String, List<Integer>> getSavedLocations() {
        return savedLocations;
    }

    public boolean addSavedLocation(String type, int location){
        if(savedLocations.containsKey(type)){
            if(!savedLocations.get(type).contains(location)){
                savedLocations.get(type).add(location);
                return true;
            }
        }
        return false;
    }

    public boolean isGroupLeader(){
        if(groupId > -1 && World.entityManager.groups.size() > 0){ // (There was one out of bounds error, adding fail safe)
            if(validGroupID())
                return World.entityManager.groups.get(groupId).getLeader() == this.getEntityID();
        }
        return false;
    }

    public boolean validGroupID(){
        try{
            int leader = World.entityManager.groups.get(groupId).getLeader();
        }catch (IndexOutOfBoundsException ex) { // TODO: remove work around and provide a real solution
            System.out.println("GroupID of " + groupId + " is out of bounds and will be corrected");
            int errorGroupID = groupId;
            for (int id : World.entityManager.groups.get(errorGroupID - 1).getEntitiesInGroup()) {
                EntityManager.entities.get(id).setGroupId(errorGroupID - 1);
            } // make sure all the others are right
        }
        return true;
    }

    public boolean taskQueueEmpty(){
        return taskQueue.isEmpty();
    }

    /* Task management revisited */
    public void taskManagement(List<List<Tile>> tileMap, int seconds){
        // manage tasks in queue first
        // The first item in the queue will be the one in progress
        // Now check to see if the first item is done
        // add items to the queue

        // checking if a current task is done
        int[] currentTile = new int[]{currTileY, currTileX};
        if(position != -1){
            //if(type == 0) System.out.println("using sub position");
            currentTile[0] = subTileY;
            currentTile[1] = subTileX;
        }// quick check
        if(!taskQueue.isEmpty()) checkTaskStatus(currentTile, tileMap, seconds);
        // update the pos
        if(position == -1){
            currentTile[0] = currTileY;
            currentTile[1] = currTileX;
        }
        // adding a new task is different if you are in a group vs not in a group
        if(groupId == -1){
            // adding a new task
            String need = getTaskNeed();
            if((taskQueue.isEmpty() || (!Objects.requireNonNull(taskQueue.peek()).getTaskType().equals(need))) && !need.equals("none") && waitTime <= 0){
                // if the need is not the same as what is currently in the queue
                //System.out.println("Identified " + need + " as next task!");
                taskLogger.write("Identified " + need + " as next task!");
                logger.write("Going to do this now : " + need);

                makeTask(need, seconds, tileMap);
                taskWaitTimer = waitTime * 2;
            }
        }else if(isGroupLeader()){
            ArrayList<String> needs = World.entityManager.groups.get(groupId).getGroupNeeds();
            if(taskQueue.isEmpty() || (!needs.contains(Objects.requireNonNull(taskQueue.peek()).getTaskType()))){
                int currNeed = 0;
                TaskUtil taskUtil = blankRef.getTaskUtil();
                for(int i = 1; i < needs.size(); i++){
                    if(taskUtil.makePriority(taskUtil.getTaskType(needs.get(i))) > taskUtil.makePriority(taskUtil.getTaskType(needs.get(currNeed)))){
                        currNeed = i;
                    }
                }//

                makeTask(needs.get(currNeed), seconds, tileMap);
                taskWaitTimer = waitTime * 2;
            }
        }

    }// end of task management

    public void checkTaskStatus(int[] currentTile, List<List<Tile>> tileMap, int seconds){
        if(idleTime >= (60 * 20)){
            idleTime = 0;
            taskQueue.clear(); // clear our queue
            System.out.println("Resetting Entity " + getEntityID());
            logger.write("I was idle for more than 20 seconds for some reason, resetting task queue.");
            if(getPosition() != -1 && type < 5) {
                //System.out.println("should change position to -1");
                if (isGroupLeader()) {
                    //System.out.println("group leader moving back to overworld [" + getPosition() + "] -> [-1]");
                    World.entityManager.groups.get(groupId).groupChangePosition(-1);
                } else if (groupId == -1) {
                    //System.out.println("not in group, going to overworld");
                    changePosition(-1);
                }
            }
            return;
        }//

        if(groupId == -1 || isGroupLeader()){
            // this is the same for both
            if(getCurrentTask().isFinished(currentTile, seconds, getPosition())){
                //System.out.println("finished task! " + getCurrentTask().getTaskType());
                //System.out.println("GPS -> " + currentTile[0] + " " + currentTile[1] + " " + getPosition());
                // reward/update
                if(groupId == -1){
                    logger.write("finished doing this : " + getCurrentTask().getTaskType());
                    switch (getCurrentTask().getTaskType().split("_")[0]) {
                        case "rest":
                            setHp(getMax_hp());
                            addXp(getCurrentTask().getXp());
                            break;
                        case "food":
                            eat(getCurrentTask().getObject());
                            addXp(getCurrentTask().getXp());
                            break;
                        case "hostile":
                            huntWaitTime = 60 * 10; // about 45 seconds
                            break;
                        case "find":
                            saveSurveyResults(getCurrentTask().getNotes());
                            break;
                    }
                }else{
                    logger.write("Our group just finished doing this : " + getCurrentTask().getTaskType());
                    World.entityManager.groups.get(groupId).completeTask();
                    saveSurveyResults(getCurrentTask().getNotes());
                    if ("hostile".equals(getCurrentTask().getTaskType())) {
                        huntWaitTime = 60 * 10; // about 45 seconds

                    }
                }
                // remove head of queue
                taskQueue.poll();
                //System.out.println("Current task is now -> " + getCurrentTask().getTaskType());
                // if were not in the overworld lets get there
                if(getPosition() != -1 && type < 5) {
                    //System.out.println("should change position to -1");
                    if(isGroupLeader()){
                        //System.out.println("group leader moving back to overworld [" + getPosition() + "] -> [-1]");
                        World.entityManager.groups.get(groupId).groupChangePosition(-1);
                    }else if(groupId == -1){
                        //System.out.println("not in group, going to overworld");
                        changePosition(-1);
                    }
                    //System.out.println("position? " + getPosition());
                }else{//
                    //System.out.println("position? " + getPosition());
                }
                if(!taskQueue.isEmpty() && !getCurrentTask().inProgress())
                    getCurrentTask().setInProgress(true);
                if(!taskQueue.isEmpty()) checkTaskStatus(currentTile, tileMap, getPosition()); // if we got rid of one, we need to check the next one
            }else{
                if(!taskQueue.isEmpty() && !getCurrentTask().inProgress())
                    getCurrentTask().setInProgress(true);
                //System.out.println("checking if needs to update task");
                // if it is still in progress, check if we need to move to a sub map
                // TODO: add a way to check for all tasks that require transitions to be made
                if(!taskQueue.isEmpty() && type < 5 && getCurrentTask().getTaskType().equals("hostile") &&
                        getCurrentTask().getTargetGPS()[0] != -1 && getCurrentTask().getTargetGPS()[2] != getPosition()){
                    if(getCurrentTask().targetTileReached(currentTile)){
                        // move position
                        if(groupId == -1){
                            //System.out.println("not in group, self change pos");
                            changePosition(getCurrentTask().getTargetGPS()[2]);
                        }
                        else if(isGroupLeader()){
                            //System.out.println("in group");
                            World.entityManager.groups.get(groupId).groupChangePosition(getCurrentTask().getTargetGPS()[2]);
                        }
                        //System.out.println("update position? " + getPosition());
                    }

                }else if(!taskQueue.isEmpty() && getCurrentTask().getTaskType().equals("interact")){
                    if(getInteractTimer() <= 0){
                        lookForEntityInteraction(seconds); // call survey
                    }

                }else if((groupId == -1 || isGroupLeader()) && !taskQueue.isEmpty() && getCurrentTask().getTaskType().contains("find")){
                    // if we get here that means our current tile does not have what we are looking for
                    if(getCurrentTask().getTaskFails() < 10) {
                        getCurrentTask().addFail();
                        List<int[]> moves = getVisibleEdges();
                        int[] moveSelected = moves.get(Util.random(moves.size())); // pick one of the edges
                        String need = "move_" + moveSelected[0] + "_" + moveSelected[1];
                        makeTask(need, seconds, tileMap);
                        taskWaitTimer = waitTime * 2;
                    }else{
                        // lets forget it
                        taskQueue.poll();
                    }
                }// finding task end
            }
            if(!taskQueue.isEmpty()) {
                if (getType() < 5 && !getCurrentTask().getTaskType().equals("hostile") && getPosition() != -1) {
                    // this meas something went wrong, lets reset our task queue
                    taskQueue.clear();
                    if (groupId == -1) {
                        //System.out.println("not in group, self change pos");
                        changePosition(-1);

                    } else if (isGroupLeader()) {
                        //System.out.println("in group");
                        World.entityManager.groups.get(groupId).groupChangePosition(-1);
                    }
                }
            }
        }// end of check if task finished

        if(taskQueue.size() > 30){
            taskQueue.clear();
        }
    }//

    public void changeEntityID(int newID){
        // okay first we need to change the group info if in a group
        World.editRefMap("remove", getPosition(), getEntityID());
        if(groupId != -1){
            World.entityManager.groups.get(groupId).removeEntityFromList(getEntityID());
            if(isGroupLeader())
                World.entityManager.groups.get(groupId).setLeader(newID);
            this.entityID = newID;
            World.entityManager.groups.get(groupId).addEntity(getEntityID());
        }else{
            this.entityID = newID;
        }
        this.targetEntityID = -1; // have to reset this
        World.editRefMap("add", getPosition(), getEntityID());

    }
    // end of Entity

    public void log(String s){
        this.logger.write(s);
    }

    public void logNoStamp(String s){
        this.logger.writeNoTimestamp(s);
    }

    public Map<String, List<Integer>> getSurveyResults(){
        if(getPosition() == -1){
            return eUtil.survey(this, World.tileMap);
        }else{
            return eUtil.survey(this, Objects.requireNonNull(World.getMap(getPosition())).getTileMap());
        }
    }// done

    private void lookForEntityInteraction(int seconds){
        List<Integer> entitiesInSight = getSurveyResults().get("entities");
        for(int id : entitiesInSight){
            if(id != this.getEntityID()){
                if(eUtil.interact(this, EntityManager.entities.get(id)))
                    break;
            }
        }
    }

    public List<int[]> getVisibleEdges(){
        return eUtil.getVisibleEdges(this);
    }

    public void makeTask(String need, int seconds, List<List<Tile>> tileMap){
        if(!need.contains("none") && (!taskQueue.isEmpty() &&  !getCurrentTask().getTaskType().contains(need))) {
            if(need.split("_")[0].contains("hostile") && (getCurrentTask() == null || !getCurrentTask().getTaskType().split("_")[0].contains("hostile"))){
                // check if we have any hostile locaitons saved, if not find one
                System.out.println("here");
                if(savedLocations.get("hostile").size() > 0){
                    System.out.println(savedLocations.get("hostile").size());
                    int hostileIdx = savedLocations.get("hostile").get(Util.random(savedLocations.get("hostile").size()));
                    need += "_" + ObjectManager.interactableObjects.get(hostileIdx).getCurrTileY() + "_" +
                                  ObjectManager.interactableObjects.get(hostileIdx).getCurrTileX() + "_" +
                                  ObjectManager.interactableObjects.get(hostileIdx).getTileMapPos();
                }else if(!taskQueue.isEmpty() && !getCurrentTask().getTaskType().contains("find")){
                    // else find it
                    System.out.println("find hostile");
                    addTask("find_hostile", seconds, tileMap);
                    taskWaitTimer = waitTime * 2;
                }
            }else if(need.split("_")[0].contains("food") && (!taskQueue.isEmpty() && !Objects.requireNonNull(getCurrentTask()).getTaskType().split("_")[0].contains("food"))) {
                if (savedLocations.get("food").size() > 0) {
                    int hostileIdx = savedLocations.get("food").get(Util.random(savedLocations.get("food").size()));
                    need += "_" + ObjectManager.interactableObjects.get(hostileIdx).getCurrTileY() + "_" +
                            ObjectManager.interactableObjects.get(hostileIdx).getCurrTileX() + "_" +
                            ObjectManager.interactableObjects.get(hostileIdx).getTileMapPos();
                } else if((!taskQueue.isEmpty() &&  (!getCurrentTask().getTaskType().contains("find") && blankRef.getTaskUtil().timeExpired(getCurrentTask(), seconds)))){
                    // else find it
                    addTask("find_food", seconds, tileMap);
                    taskWaitTimer = waitTime * 2;
                }
            }else {
                System.out.println(need);
                TaskRef task = new TaskRef(getEntityID(), need,
                        new int[]{getCurrTileY(), getCurrTileY(), getPosition()},
                        tileMap, seconds);

                if (task.getTaskUtil().isValid(task)) {
                    if (!taskQueue.isEmpty() && task.getPriority() > getCurrentTask().getPriority()) {
                        getCurrentTask().setInProgress(false);
                        task.setInProgress(true);
                    }
                    logger.write("Going to do this now : " + need);
                    taskQueue.add(task);
                    taskWaitTimer = waitTime * 3;
                } else
                    taskLogger.write("invalid task, not adding to queue! -> " + task.getTaskType() + " [" + task.getTargetGPS()[0] + task.getTargetGPS()[1] + "]");
            }
        }
    }

    // TODO: stop task duplicaiton with find
    public void addTask(String need, int seconds, List<List<Tile>> tileMap){
        System.out.println(need);
        TaskRef task = new TaskRef(getEntityID(), need,
                new int[]{getCurrTileY(), getCurrTileY(), getPosition()},
                tileMap, seconds);

        if (task.getTaskUtil().isValid(task)) {
            if (!taskQueue.isEmpty() && task.getPriority() > getCurrentTask().getPriority()) {
                getCurrentTask().setInProgress(false);
                task.setInProgress(true);
            }
            logger.write("Going to do this now : " + need);
            taskQueue.add(task);
            taskWaitTimer = waitTime * 3;
        } else
            taskLogger.write("invalid task, not adding to queue! -> " + task.getTaskType() + " [" + task.getTargetGPS()[0] + task.getTargetGPS()[1] + "]");
    }

    // saves a type of object
    // TODO: remembering based on personality
    public void saveSurveyResults(String note){
        System.out.println("saving note: " + note);
        for(int id : getSurveyResults().get("objects")){
            String type = ObjectManager.interactableObjects.get(id).getType().split("_")[1];
            if(type.contains(note)) {
                if (savedLocations.containsKey(type)) {
                    if (!savedLocations.get(type).contains(id))
                        savedLocations.get(type).add(id);
                } else {
                    // create a new key
                    savedLocations.put(type, new ArrayList<>());
                    savedLocations.get(type).add(id);
                }
            }
        }
    }


}
