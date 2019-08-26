package luna.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import luna.entity.Entity;
import luna.world.objects.InteractableObject;

// Will assist the world lookup, making finding info/entities/objects much easier
// Each tile will have a list for entities, objects, and maybe some extra tile info
public class Tile {
	//
	private int xPos, yPos;
	private List<Entity> entitiesInTile = Collections.synchronizedList(new ArrayList<Entity>());
	private List<InteractableObject> objectsInTile = Collections.synchronizedList(new ArrayList<InteractableObject>()) ;
	
	public Tile(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
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
