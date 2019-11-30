package luna.entity;

import luna.util.Animation;
import luna.util.Logger;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity implements Actions{
    // position stuff
    protected int x, y, lastX, lastY, world_w, world_h;
    protected int waitTime = 0, maxWaitTime = 70; // needs to be greater than ticks per second
    protected int attackTimer = 0, attackWaitTime = 35;
    protected int dmgTimer = 0, dmgWaitTime = 5; // less because entities can attack quickly
    private int entityID;
    //
    protected Task currentTask;
    protected Task savedTask;
    protected List<Task> nextTasks = new ArrayList<>();
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
    protected int hp, max_hp, xp, max_xp, drop_xp, dmg, level;
    protected int hunger, max_hunger, hunger_loss_rate;
    protected Color entity_base_color = new Color(255, 102, 153);

    protected Color hp_color = new Color(255, 16, 38, 160);
    protected Color hungerColor = new Color(255, 130, 14, 182);
    protected Color shadow = new Color(0,0,0,130);
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

    protected int currTileX, currTileY;
    protected int numCollisions = 0, collisionTimer = 0;

    protected int targetEntityID = -1;
    protected boolean targetAdjacent = false;
    protected List<InteractableObject> objectsOnPerson = new ArrayList<>();

    protected List<List<Integer>> movesLeft = new ArrayList<>();
    // this will be called by the constructors to give our guys their base stat
    //  values.

    protected Logger logger;

    // TODO: personality changes
    //  - Adding actions based on personality
    //  - Interacting with other entities
    // TODO: Sub classes of entities (sub class defined further definitions required)
    //  - Define races/other types of entities
    
    public void set_stats(){
        this.size = world_scale;
        this.logger = new Logger("./logs/EntityLogs/entity_" + this.entityID + ".txt");
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
        this.currentTask = new Task(new int[]{currTileX, currTileY}, 0,this.entityID);
        this.savedTask = new Task(new int[]{currTileX, currTileY}, 0,this.entityID);

        logger.write("Entity " + this.entityID);
        logger.write("hp:  " + this.max_hp);
        logger.write("dmg: " + this.dmg);
        logger.write("pos: [" + this.currTileY + " " + this.currTileY + "]");

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
        set_stats();
        makeImages();
    }//
    public Entity(int x, int y, int world_w, int world_h, int world_scale, int id){
        this.x = x;
        this.y = y;
        this.world_h = world_h;
        this.world_w = world_w;
        this.world_scale = world_scale;
        this.entityID = id;
        set_stats();
        makeImages();
    }//

    public Entity(int x, int y, int world_w, int world_h, int world_scale, Color c, int id){
        this.x = x;
        this.y = y;
        this.world_h = world_h;
        this.world_w = world_w;
        this.entity_base_color = c;
        this.world_scale = world_scale;
        this.entityID = id;
        set_stats();
        makeImages();

    }//

    // draw our guy
    public void render(Graphics2D g){
        if (position == -1) {
            g.setColor(entity_base_color);
            //g.fillRect(this.x,this.y,this.size*world_scale,this.size);
            drawHpBar(g);
            animationMap.get(currentAnimation).drawAnimation(g, this.x, this.y, size, size);
            g.setColor(Color.black);

            if (currentTask.targetTile[0] != -1) {
                //g.setColor(shadow);
                //g.fillRect(currentTask.startPos[1] * world_scale, currentTask.startPos[0] * world_scale, world_scale, world_scale);
                //g.fillRect(currentTask.targetTile[1] * world_scale, currentTask.targetTile[0] * world_scale, world_scale, world_scale);

            }
        }
        // for now this will just be a debug functionality
        else{
            //first lets draw the sub tile map off to the side of the screen
            //System.out.println("X " + World.subMaps.get(position).getRenderXStart()+this.subX);
            //System.out.println("Y " + World.subMaps.get(position).getRenderYStart()+this.subY);
            animationMap.get(currentAnimation).drawAnimation(g, World.subMaps.get(position).getRenderXStart()+this.subX,
                                                                World.subMaps.get(position).getRenderYStart()+this.subY,
                                                                   size, size);
            drawHpBar(g,World.subMaps.get(position).getRenderXStart()+this.subX, World.subMaps.get(position).getRenderYStart()+this.subY);
        }
        //Rectangle bound = this.getBounds();
        //g.drawRect(bound.x, bound.y, bound.width, bound.height);
        //bound = null;
        
    }//

    // move here
    public void update(List<List<Tile>> tileMap, int seconds){
        //System.out.println(position);
        lastX = x;
        lastY = y;
        if(position != -1){
            lastSubX = subX;
            lastSubY = subY;
            //System.out.println("Pos [" + subX + " , " + subY + "]");
        }//
        animationMap.get(currentAnimation).runAnimation();
        //System.out.println(toString());
        //

        taskManagement(tileMap, seconds);
        moveManagement(seconds);
        if(targetEntityID != -1) attack(targetEntityID);
        hungerManagement(seconds);

        // animation calls
        currentAnimation = Util.intToStringDirectionMap.get(direction);
        //

        // there can be an issue with collisions when transitioning from a sub map to the overworld
        if(collisionTimer == 0) collisionManagement(tileMap);

        // other managements here
        if(waitTime > 0)
            waitTime--;
        if(collisionTimer > 0)
            collisionTimer--;
        if(attackTimer > 0)
            attackTimer--;
        if(dmgTimer > 0)
            dmgTimer--;

    }///

    // walk around randomly
    public void wander(){
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

    // moving in sub maps
    public void subMapMovement(){
        // TODO: Define how moving in sub maps will work
        //  - should be based on the task, entities enter a sub map for a reason

        // TODO: add attack condition here
        if (currentTask.isTaskSet() && (currentTask.getGoal() == 0 || currentTask.getGoal() == 4 || currentTask.getGoal() == 7)) {
            // If we do not have a goal wander
            if(targetEntityID == -1) wander();
            setSubMapTarget();
            moveToTarget();
            // lets check if the encounter we are in is active
            if(currentTask.getObjectID() != -1 &&
                    !World.tileMap.get(currTileY).get(currTileX).getObjectsInTile().get(currentTask.getObjectID()).isActive()){
                changePosition(-1);
            }
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
        //System.out.println(this.x + " " + this.y);
        //System.out.println((this.world_w-this.size-1) + " " + (this.world_h-this.size-1));
        if(this.x <= 0 || this.y <= 0 || this.x >= this.world_w || this.y >= this.world_h) {
            return true;
        }
        return false;
    }// end of collision
    public boolean collision(List<List<Tile>> tileMap){
        //System.out.println("sub Pos " + this.subX + " " + this.subY);
        //System.out.println("sub map " + (tileMap.size()*world_scale) + " " + (tileMap.size()*world_scale));
        if(this.subX <= 0 || this.subY <= 0 || this.subX >= (tileMap.size())*world_scale || this.subY >= (tileMap.size())*world_scale){
            return true;
        }
        return false;
    }// end of collision

    // other helpers

    // A helper to draw the hp bar based on the current health
    public void drawHpBar(Graphics2D g){
        g.setColor(hp_color);
        int barWidth = (size * this.hp) / this.max_hp;
        int hungerBarWidth = (size * this.hunger) / this.max_hunger;
        g.fillRect(this.x-1,this.y-5, barWidth, 2);
        g.setColor(hungerColor);
        g.fillRect(this.x-1,this.y-3, hungerBarWidth, 2);
        g.setColor(this.shadow);
        //Rectangle contact = this.getContactBounds();
        //g.fillRect(contact.x,contact.y,contact.width,contact.height);
    }

    public void drawHpBar(Graphics2D g, int x, int y){
        g.setColor(hp_color);
        int barWidth = (size * this.hp) / this.max_hp;
        int hungerBarWidth = (size * this.hunger) / this.max_hunger;
        g.fillRect(x-1,y-5, barWidth, 2);
        g.setColor(hungerColor);
        g.fillRect(x-1,y-3, hungerBarWidth, 2);
        g.setColor(this.shadow);
        //Rectangle contact = this.getContactBounds();
        //g.fillRect(contact.x,contact.y,contact.width,contact.height);
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
        if(currentTask.moves.size() > 0 && position == -1){
            // else we don't need to do anything
            if(currTileY != currentTask.moves.get(0).get(0)){
                // move vert
                if(currTileY > currentTask.moves.get(0).get(0))
                    move(2);
                else if(currTileY < currentTask.moves.get(0).get(0))
                    move(3);
            }
            if(currTileX != currentTask.moves.get(0).get(1)){
                // move herz
                if(currTileX > currentTask.moves.get(0).get(1))
                    move(0);
                else if(currTileX < currentTask.moves.get(0).get(1))
                    move(1);
            }
            // if we are at the next move now, we need to remove it;
            if(currTileY == currentTask.moves.get(0).get(0) && currTileX == currentTask.moves.get(0).get(1)){
                currentTask.moves.remove(0);
            }
        }else if(currentTask.moves.size() > 0 && position != -1){
            // else we don't need to do anything
            if(subTileY != currentTask.moves.get(0).get(0)){
                // move vert
                if(subTileY > currentTask.moves.get(0).get(0))
                    move(2);
                else if(subTileY < currentTask.moves.get(0).get(0))
                    move(3);
            }
            if(subTileX != currentTask.moves.get(0).get(1)){
                // move herz
                if(subTileX > currentTask.moves.get(0).get(1))
                    move(0);
                else if(subTileX < currentTask.moves.get(0).get(1))
                    move(1);
            }
            // if we are at the next move now, we need to remove it;
            if(subTileY == currentTask.moves.get(0).get(0) && subTileX == currentTask.moves.get(0).get(1)){
                currentTask.moves.remove(0);
            }
        }
    }

    // a collision between other entities

    // basic compareTO, later I will make a detailed compareTo that compares stats and traits
    public int compareTo(Entity e){
    	if(this.entityID == e.getEntityID()) return 1;
    	else return 0;
    }

    //
    // For log reporting
    // after every iteration an entity will have a status based on health, hunger, happiness etc.
    // TODO add status logic once logging is setup
    public String makeStatusMessage(){
        return "I'm Oaky";
    }

	@Override
	public void attack(int entityID) {
		if(attackTimer == 0 && targetAdjacent && hp > 0){
		    System.out.println("[" + getEntityID() + "] Attack [" + entityID + "]");
		    attackTimer = attackWaitTime;
		    try{
		        if(World.entities.get(entityID).isAlive())
		            World.entities.get(entityID).takeDmg(getDmg());
		        else{
		            addXp(World.entities.get(entityID).getDrop_xp());
		            targetEntityID = -1;
                }
            }catch (Exception ex){
		        System.out.println("Error accessing entity " + entityID);
		        System.out.println("Called from attack in entity " + getEntityID());
		        System.exit(1);
            }
        }//
	}// end of attack

	@Override
	public void eat(InteractableObject e) {
		
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

    public void shutdown(){
        this.logger.closeWriter();
        this.currentTask.logger.closeWriter();
    }

    public String toString(){
        return "Entity :: " + this.entityID +
             "\nHP     :: " + this.hp +
             "\nHunger :: " + this.hunger +
             "\n";
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    // Manage tasks, can change based on type of entity
    public void taskManagement(List<List<Tile>> tileMap, int seconds){
        // Task Management block
        // move based on current task
        /* Task management */
        if(!currentTask.isTaskSet()) {
            System.out.println("Current Task not set -> goal = " + currentTask.getGoal());
            if(position != -1 && currentTask.getGoal() == 1){
                changePosition(-1);
                World.visibleMap = -1;
            }
            if(position == -1){
                currentTask.startPos[0] = currTileY;
                currentTask.startPos[1] = currTileX;
            }else{
                currentTask.startPos[0] = subTileX;
                currentTask.startPos[1] = subTileY;
            }

            if(position == -1) {
                currentTask.makeTask(tileMap, seconds);
            }
            else {
                currentTask.makeTask(World.subMaps.get(this.position).getTileMap(), seconds);
            }
        }

        // DEFINING GOALS
        // setting the goal
        if(this.hp > this.max_hp*.50 && Math.random()*100 > 75 && waitTime <= 0 && currentTask.getGoal() != 7){
            currentTask.setGoal(7);
            System.out.println("moving to hostile tile");
        }else{
            if(waitTime <= 0 && currentTask.getGoal() != 7){
                System.out.println("Tried to assign a hostile but failed");
                waitTime = maxWaitTime;
            }

        }
        // hunger goal can override the rest goal, due to hunger affecting health as well
        if(this.hunger < this.max_hunger*.50 && currentTask.getGoal() != 1) {
            //
            //checkTask();
            currentTask.setGoal(1);
            // travel to the over world too

        }
        if(this.hp < this.max_hp*.50 && currentTask.getGoal() != 2 && currentTask.getGoal() != 1){
            //checkTask();
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
                changePosition(currentTask.targetMapPos);
                World.visibleMap = position;
                subX = 5; // setting to 0 could mess with collision
                subY = 5;

                direction = Util.stringToIntDirectionMap.get("down");
            }

        }else if(position != -1){
            if(currentTask.isTaskFinished(new int[]{subTileY, subTileX}, seconds)){
                if(currentTask.getGoal() == 1) {
                    currentTask.setGoal(4);
                    hunger = max_hunger;
                }
                if(currentTask.getGoal() == 6){

                }

            }
        }
    }// END OF TASK MANAGEMENT

    // manage movement (All kinds)
    public void moveManagement(int seconds){
        // MOVEMENT
        // moving in sub maps
        if(position != -1){
            subMapMovement();
        }else {
            // moving on the overworld
            // move based on task set
            if (currentTask.isTaskSet() && (currentTask.getGoal() == 0 || currentTask.getGoal() == 4)) {
                // If we do not have a goal wander
                wander();
            } else {
                // move to target
                executeMoves();
            }
        }
    }// END OF MOVEMENT

    public void hungerManagement(int seconds){
        // hunger management
        if(seconds > 0 && seconds % 5 == 0 && waitTime <= 0){
            if(this.hunger > 0){
                this.hunger -= this.hunger_loss_rate;
                waitTime = maxWaitTime;
            }
        }
    }// end of hunger management

    // collision module
    public void collisionManagement(List<List<Tile>> tileMap){
        /*collision and location management*/
        if(position == -1) {
            if (collision()) {
                x = lastX;
                y = lastY;
            }
            int tileX = (x / world_scale);
            int tileY = (y / world_scale);
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
                //System.out.println("Sub collision");
            }
            int tileX = (subX / world_scale);
            int tileY = (subY / world_scale);
            //
            if (tileX != subTileX || tileY != subTileY) {
                try { // Bounds error handling
                    if (tileY >= tempWorldSize)
                        tileY = tempWorldSize - 1;
                    if (tileX >= tempWorldSize)
                        tileX = tempWorldSize - 1;
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

    // if we have a task that is not goal 0 or 4 then we save the task in saved task
    public void checkTask(){
        if(currentTask.getGoal() != 0 || currentTask.getGoal() != 4 || currentTask.getGoal() != 2){
            savedTask.clone(currentTask);
        }
    }

    // set the current task to the saved task
    // - also will reset the saved task to a zero goal
    public void restoreTask(List<List<Tile>> tileMap, int seconds){
        currentTask.logger.write("restoring current task");
        currentTask.clone(savedTask);
        savedTask.setGoal(0);
        // we may need to repathfind based on where we are
        if(position != currentTask.getTargetMapPos()){
            // Probs means we left the tilemap to find something else
            //  we need to pathfind to get back to it
            currentTask.makeTask(tileMap,seconds);
        }

    }// end

    // TODO: Add targeting other entities
    public void setSubMapTarget(){
        // Needs to survey the are and see if any entities that are targets of this entity
        if(position != -1){
            if(targetEntityID != -1){
                // check if the entity is alive still

            }else if(targetEntityID == -1){
                int kernel = 3, kx, ky;
                int mapSize = World.subMaps.get(position).getTileMap().size()-1;
                for(int y = -1; y < kernel-1; y++){
                    ky = subTileY + y;
                    for(int x = -1; x < kernel-1; x++){
                        kx = subTileX + x;
                        if(ky >= 0 && kx >= 0 && ky <= mapSize && kx <= mapSize){
                            for(Entity tmp : World.subMaps.get(position).getTileMap().get(ky).get(kx).getEntitiesInTile()){
                                if(tmp.isAlive() && tmp.getEntityID() != this.getEntityID() && getType() != tmp.getType()){
                                    targetEntityID = tmp.getEntityID();
                                    System.out.println("Target Set -> \n" + World.entities.get(targetEntityID).toString());
                                    //System.exit(1);
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
        if(targetEntityID != -1){
            if(World.entities.get(targetEntityID).getSubX() < getSubX()-world_scale){
                System.out.println("Moving Left to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("left"));
                movesMade++;
            }
            if(World.entities.get(targetEntityID).getSubX() > getSubX()+world_scale){
                System.out.println("Moving Right to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("right"));
                movesMade++;
            }
            if(World.entities.get(targetEntityID).getSubY() < getSubY()-world_scale){
                System.out.println("Moving Up to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("up"));
                movesMade++;
            }
            if(World.entities.get(targetEntityID).getSubY() > getSubY()+world_scale){
                System.out.println("Moving Down to target " + this.getEntityID());
                move(Util.stringToIntDirectionMap.get("down"));
                movesMade++;
            }
            if(movesMade == 0)
                targetAdjacent = true;
            else
                targetAdjacent = false;
        }//
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
    public void takeDmg(int dmg){
        if(dmgTimer == 0){
            dmgTimer = dmgWaitTime;
            // TODO: Add knockback mechanic
            hp -= dmg;
            if(hp <= 0){
                hp = 0;
                World.subMaps.get(position).makeEntityRefs();
                // dead
                // TODO: Add death mechanic
            }
        }

    }//

    public int getHp() {
        return hp;
    }

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

    // example, if an enemy has an agro stat to draw attackers
    public void setTargetEntityID(int targetEntityID) {
        this.targetEntityID = targetEntityID;
    }

    public boolean isAlive(){return hp > 0;}

    public void changePosition(int pos){
        // changing
        System.out.println("Changing positions");
        position = pos;
        collisionTimer = 5;
        if(position > -1){
            World.subMaps.get(position).makeEntityRefs(); // add itself to the refs
        }
    }

    public void addXp(int xp){
        this.xp += xp;
        if(this.xp >= max_xp){
            level++;
            this.xp = this.xp-max_xp;
            max_xp = max_xp + (int) (.1 * max_xp); // add 10%
            max_hp = max_hp + (int) (.1 * max_hp) + Util.random(2);
            if(level % 2 == 0) dmg = dmg + (int) (.15 * dmg) + Util.random(2);
            drop_xp = drop_xp + (int) (.25 * drop_xp);

            logger.write("Level Up! ------");
            logger.writeNoTimestamp("HP  : " + max_hp);
            logger.writeNoTimestamp("DMG : " + dmg);
            logger.writeNoTimestamp("XP  : (" + xp + "/" + max_xp + ")");
            logger.writeNoTimestamp("-----------------------");
        }
    }
}
