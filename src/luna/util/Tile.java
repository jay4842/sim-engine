package luna.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import luna.entity.Entity;
import luna.world.objects.Food;
import luna.world.objects.InteractableObject;

// Will assist the world lookup, making finding info/entities/objects much easier
// Each tile will have a list for entities, objects, and maybe some extra tile info
public class Tile {
	//
	private int xPos, yPos, tile_id, tile_type;
	private List<Entity> entitiesInTile = Collections.synchronizedList(new ArrayList<Entity>());
	private List<InteractableObject> objectsInTile = Collections.synchronizedList(new ArrayList<InteractableObject>()) ;
	
	public Tile(int xPos, int yPos, int tile_id, int world_scale, int world_h, int world_w, int tile_type) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.tile_id = tile_id;
		this.tile_type = tile_type;

		// chance to add food to tile
        if(Math.random()*100 > 95)
            this.objectsInTile.add(new Food(xPos,yPos,"food_apple", this.objectsInTile.size(),world_h,world_w,world_scale));
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
}
