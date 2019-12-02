package luna.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import luna.entity.Entity;
import luna.world.World;
import luna.world.objects.Food;
import luna.world.objects.HostileEncounter;
import luna.world.objects.InteractableObject;
import luna.world.objects.ObjectOfInterest;

// Will assist the world lookup, making finding info/entities/objects much easier
// Each tile will have a list for entities, objects, and maybe some extra tile info
public class Tile {
	//
	private int xPos, yPos, tile_id, tile_type, world_h, world_w, world_scale;
	private List<Entity> entitiesInTile = Collections.synchronizedList(new ArrayList<Entity>());
	private List<InteractableObject> objectsInTile = Collections.synchronizedList(new ArrayList<InteractableObject>()) ;
	private int tileMapPos = -1; // position

	public Tile(int xPos, int yPos, int tile_id, int world_scale, int world_h, int world_w, int tile_type) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.tile_id = tile_id;
		this.tile_type = tile_type;
		this.world_h = world_h;
		this.world_w = world_w;
		this.world_scale = world_scale;
		setupObjects();
	}

	public Tile(int xPos, int yPos, int tile_id, int world_scale, int world_h, int world_w, int tile_type, int tileMapPos) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.tile_id = tile_id;
		this.tile_type = tile_type;
		this.world_h = world_h;
		this.world_w = world_w;
		this.world_scale = world_scale;
		this.tileMapPos = tileMapPos;
		//System.out.println("tileType " + tile_type + " mapPos " + tileMapPos);
		setupObjects();
	}

	// Object types type_subType_position_objectID
	public void setupObjects(){
		// normal tile with chance generation
        int mapPos = World.getMapListSize();
        if(mapPos > 0) mapPos--;
		if(tile_type == 0) {
			if (Math.random() * 100 > 98)
				this.objectsInTile.add(new Food(xPos, yPos, "food_apple_"+this.tileMapPos+"_"+this.objectsInTile.size(), this.objectsInTile.size(), world_h, world_w, world_scale));
			else if (Math.random() * 100 > 98)
				this.objectsInTile.add(new HostileEncounter(xPos, yPos, "hostile_F_"+ mapPos +"_"+this.objectsInTile.size(), this.objectsInTile.size(), world_h, world_w, world_scale));
		}
		else if(tile_type == 1){
			// is a food tile without chance
			this.objectsInTile.add(new Food(xPos, yPos, "food_apple_"+this.tileMapPos+"_"+this.objectsInTile.size(), this.objectsInTile.size(), world_h, world_w, world_scale));
		}
		else if(tile_type == 2){
			// this means that this tile will have a hostile without chance
			this.objectsInTile.add(new HostileEncounter(xPos, yPos, "hostile_F_"+ mapPos +"_"+this.objectsInTile.size(), this.objectsInTile.size(), world_h, world_w, world_scale));
		}
		else if(tile_type == 3 && tileMapPos != -1){
			// is a food tile without chance
			this.objectsInTile.add(new Food(xPos, yPos, "food_apple_" +this.tileMapPos+"_"+this.objectsInTile.size(), this.objectsInTile.size(), world_h, world_w, world_scale));
		}
		// others later

	}

	// render
    public void render(Graphics2D g2d){
        Iterator<InteractableObject> objectIterator = objectsInTile.iterator();
        synchronized (objectIterator){
            while(objectIterator.hasNext()){
                objectIterator.next().render(g2d);
            }
        }
    }

	public void render(Graphics2D g2d, int x, int y){
		Iterator<InteractableObject> objectIterator = objectsInTile.iterator();
		synchronized (objectIterator){
			while(objectIterator.hasNext()){
				objectIterator.next().render(g2d, x, y);
			}
		}
	}

    // update
    public void update(int seconds){
        for(int i = 0; i < objectsInTile.size(); i++){
            objectsInTile.get(i).update(seconds);
            if(objectsInTile.get(i).isDestroyed()) {
                objectsInTile.remove(i);
                i--;
            }
        }
    }

	// Getters and setters below
	public int getxPos() { return xPos; }

	public void setxPos(int xPos) { this.xPos = xPos; }

	public int getyPos() { return yPos; }

	public void setyPos(int yPos) { this.yPos = yPos; }

	public List<Entity> getEntitiesInTile() { return entitiesInTile; }
	public void addEntity(Entity e){
		for(int i = 0; i < this.entitiesInTile.size(); i++) {
			if (this.entitiesInTile.get(i).getEntityID() == e.getEntityID())
				return;
		}
		this.entitiesInTile.add(e);
	}
	// remove entity by ID
	public boolean removeEntity(int entityID){
		for(int i = 0; i < this.entitiesInTile.size(); i++){
			if(this.entitiesInTile.get(i).getEntityID() == entityID){
				entitiesInTile.remove(i);
				return true;
			}//
		}//
		return false;
	}//

	public void setEntitiesInTile(List<Entity> entitiesInTile) { this.entitiesInTile = entitiesInTile; }

	// extra object options, very similar to the entity logic
	public void addObject(InteractableObject o){
		for(int i = 0; i < this.objectsInTile.size(); i++){
			if(this.objectsInTile.get(i).getObjectID() == o.getObjectID())
				return;
		}
		this.objectsInTile.add(o);
	}//
	//
	public boolean removeObject(int objectID) {
		for (int i = 0; i < this.objectsInTile.size(); i++) {
			if (this.objectsInTile.get(i).getObjectID() == objectID) {
				this.objectsInTile.remove(i);
				return true;
			}
		}
		return false;
	}
	public List<InteractableObject> getObjectsInTile() { return objectsInTile; }

	public void setObjectsInTile(List<InteractableObject> objectsInTile) { this.objectsInTile = objectsInTile; }

	public int getTileMapPos() {
		return tileMapPos;
	}

	public void setTileMapPos(int tileMapPos) {
		this.tileMapPos = tileMapPos;
	}

	public String toString(){
		String line = "";
		line += "[" + this.xPos + " " + this.yPos + "] ";
		line += "object count = " + this.objectsInTile.size() + " ";
		for(InteractableObject obj : this.objectsInTile){
			line += "" + obj.getType() + " ";
		}
		return line;
	}
}
