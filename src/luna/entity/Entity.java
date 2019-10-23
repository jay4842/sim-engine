package luna.entity;

import luna.util.Animation;
import luna.util.Tile;
import luna.util.Util;
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
    private int entityID;
    //
    protected Task currentTask;
    protected List<Task> nextTasks = new ArrayList<>();
    //

    // entity specific stuff
    // - these guys will help explain what an entity is/what makes it unique
    protected int size = 5;
    protected int hp, max_hp, xp, max_xp, drop_xp, dmg;
    protected int hunger, max_hunger, hunger_loss_rate;
    protected Color entity_base_color = new Color(255, 102, 153);

    protected Color hp_color = new Color(255, 77, 77,100);
    protected Color hungerColor = new Color(255, 130, 14, 182);
    protected Color shadow = new Color(0,0,0,130);
    // Map of spriteSheets
    protected Map<String, BufferedImage[]> spriteSheetMap = new HashMap<String, BufferedImage[]>();
    // map from string direction to actual direction values
    protected Map<String, Integer> stringToIntDirectionMap = new HashMap<>();
    protected Map<Integer, String> intToStringDirectionMap = new HashMap<>();
    // our map of animations
    protected Map<String, Animation> animationMap = new HashMap<>();
    // our current frame
    protected String currentAnimation = "Down";

    // moving variables
    private int moves = 0, move_wait = 10, max_moves = 25;
    private int direction = 0, velocity = 2;
    private Point target_point = new Point(-1,-1);
    int world_scale = 1;
    int currTileX, currTileY;

    protected List<InteractableObject> objectsOnPerson = new ArrayList<>();
    // this will be called by the constructors to give our guys their base stat
    //  values.
    public void set_stats(){
        this.max_hp = (int)(Math.random() * 3) + 3;
        this.dmg = (int)(Math.random() * 3) + 1;
        this.hp = max_hp;
        this.xp = 0;
        this.max_xp = 3;
        this.drop_xp = 1;
        this.max_hunger = 10;
        this.hunger = this.max_hunger;
        this.hunger_loss_rate = 1;

        //
        this.currTileX = x / world_scale;
        this.currTileY = y / world_scale;
        this.currentTask = new Task(new int[]{currTileX, currTileY}, 0);

    }

    // some other setups
    public void makeImages(){
    	// These are just the idle images/ moving images
    	// - other frames will be added later
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
        stringToIntDirectionMap.put("Left", 0);
        stringToIntDirectionMap.put("Right", 1);
        stringToIntDirectionMap.put("up", 2);
        stringToIntDirectionMap.put("down", 3);
        // and vise versa
        intToStringDirectionMap.put(0,"Left");
        intToStringDirectionMap.put(1,"Right");
        intToStringDirectionMap.put(2,"Up");
        intToStringDirectionMap.put(3,"Down");
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
        g.setColor(entity_base_color);
        //g.fillRect(this.x,this.y,this.size*world_scale,this.size);
        drawHpBar(g);
        animationMap.get(currentAnimation).drawAnimation(g,this.x,this.y,world_scale,world_scale);
        g.setColor(Color.black);
        //Rectangle bound = this.getBounds();
        //g.drawRect(bound.x, bound.y, bound.width, bound.height);
        //bound = null;
        
    }//

    // move here
    public void update(List<List<Tile>> tileMap, int seconds){
        lastX = x;
        lastY = y;
        animationMap.get(currentAnimation).runAnimation();
        //

        // move based on current task
        /* Task management */
        if(!currentTask.isTaskSet())
            currentTask.makeTask(tileMap, seconds);
        // move based on task
        if(currentTask.isTaskSet() && (currentTask.getGoal() == 0 || currentTask.getGoal() == 4)){
            // If we do not have a goal wander
            wander();
        }else if(currentTask.isTaskSet() && currentTask.getGoal() != 0){
            // TODO: add finish task action once finish criteria is established
        }
        //

        //
        currentAnimation = intToStringDirectionMap.get(direction);
        //
        /*collision and location management*/
        if(collision()){
           x = lastX;
           y = lastY;
        }
        int tileX = (x / world_scale);
        int tileY = (y / world_scale);
        //
        if(tileX != currTileX || tileY != currTileY){
            try { // Bounds error handling
                if(tileY >= tileMap.size())
                    tileY = tileMap.size() - 1;
                if(tileX >= tileMap.size())
                    tileX = tileMap.get(0).size() - 1;
                tileMap.get(currTileY).get(currTileX).removeEntity(this.entityID);
                tileMap.get(tileY).get(tileX).addEntity(this);
                currTileX = tileX;
                currTileY = tileY;
            }catch (Exception ex){
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
            this.move_wait --;
        }
    }

    // use a switch to decide which direction to move
    @Override
    public void move(int direction){
        //System.out.println(direction);
        switch (direction){
            case 0:{
                // move left
                this.x -= this.velocity;
                break;
            }
            case 1:{
                // move right
                this.x += this.velocity;
                break;
            }
            case 2:{
                // move up
                this.y -= this.velocity;
                break;
            }
            case 3:{
                // move down
                this.y += this.velocity;
                break;
            }
            default:
                break;
        }// end of switch
    }// end of move

    // return true if we go out of bounds
    public boolean collision(){
        if(this.x <= 0 || this.y <= 0 || this.x >= this.world_w-this.size-1 || this.y >= this.world_h-this.size-1) {
            //System.out.println(this.x + " " + this.y);
            //System.out.println(this.world_w + " " + this.world_h);
            //System.out.println("Collision detected");
            return true;
        }
        return false;
    }// end of collision

    // other helpers

    // A helper to draw the hp bar based on the current health
    public void drawHpBar(Graphics2D g){
        g.setColor(hp_color);
        int barWidth = (7 * this.hp) / this.max_hp;
        int hungerBarWidth = (7 * this.hunger) / this.max_hunger;
        g.fillRect(this.x-1,this.y-5, barWidth, 2);
        g.setColor(hungerColor);
        g.fillRect(this.x-1,this.y-3, hungerBarWidth, 2);
        g.setColor(this.shadow);
        //Rectangle contact = this.getContactBounds();
        //g.fillRect(contact.x,contact.y,contact.width,contact.height);
    }

    // a bound helper
    public Rectangle getBounds(){
        return new Rectangle(this.x,this.y,this.world_scale,this.world_scale);
    }// end of get bounds

    // this guy returns a rectangle of the contact bounds
    // so if this contact bound intersects with anothers contact bound
    //  they could then choose to interact with one another.
    public Rectangle getContactBounds(){
        return new Rectangle(this.x-this.size*2, this.y-this.size*2, (this.size*2)*3,(this.size*2)*3);
    }

    //
    public void moveToPoint(Point cord){
    	// TODO: add simple move to point logic
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
	public void attack(Entity e) {
		
	}

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
}
