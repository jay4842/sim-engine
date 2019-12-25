package luna.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import luna.entity.Entity;
import luna.world.World;
import luna.world.objects.InteractableObject;
import luna.world.util.ObjectManager;
import luna.world.util.TileParameters;

// Will assist the world lookup, making finding info/entities/objects much easier
// Each tile will have a list for entities, objects, and maybe some extra tile info
public class Tile {
	//
	private int xPos, yPos, tile_id, tile_type, world_h, world_w, world_scale;
	private List<Integer> entitiesInTile = Collections.synchronizedList(new ArrayList<>());
	private List<Integer> objectsInTile = Collections.synchronizedList(new ArrayList<>()) ;
	private int tileMapPos = -1; // position
	private boolean test = false;

	public Tile(int xPos, int yPos, int tile_id, int world_scale, int world_h, int world_w, int tile_type) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.tile_id = tile_id;
		this.tile_type = tile_type;
		this.world_h = world_h;
		this.world_w = world_w;
		this.world_scale = world_scale;
		setupObjects(-1);
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
		setupObjects(this.tileMapPos);
	}

	public Tile(int xPos, int yPos, int tile_id, int world_scale, int world_h, int world_w, int tile_type, int tileMapPos, boolean test) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.tile_id = tile_id;
		this.tile_type = tile_type;
		this.world_h = world_h;
		this.world_w = world_w;
		this.world_scale = world_scale;
		this.tileMapPos = tileMapPos;
		this.test = test;
		//System.out.println("tileType " + tile_type + " mapPos " + tileMapPos);
		setupObjects(this.tileMapPos);
	}


	// Object types type_subType_position_objectID
	public void setupObjects(int tileMapPos){
		// normal tile with chance generation
		TileParameters parms;
		if(tile_type == 0) {
			if (Math.random() * 100 > 98) {
				parms = new TileParameters(xPos,yPos,"food_apple_" + tileMapPos, world_h, world_w, world_scale, test);
				int id = (int)World.callManager("post_createObjectFromTile", parms);
				if(id != -1)
					this.objectsInTile.add(id);
			}else if (Math.random() * 100 > 98) {
				tileMapPos = World.getMapListSize()-1;
				if(tileMapPos < 0) tileMapPos = 0;
				parms = new TileParameters(xPos, yPos,"hostile_F_", world_h, world_w, world_scale, test);
				int id = (int)World.callManager("post_createObjectFromTile", parms);
				if(id != -1) {
					InteractableObject obj = (InteractableObject)World.callManager("get_object", id);
					World.addMap(obj.getTileMap(), id);
					System.out.println("hostile id added " + id);
					this.objectsInTile.add(id);
				}
			}else if (Util.random(100) > 90){
				int id = -1;
				if(Util.random(100) > 50) {
					parms = new TileParameters(xPos, yPos, "resource_wood_" + tileMapPos, world_h, world_w, world_scale, test);
					id = (int)World.callManager("post_createObjectFromTile", parms);
				}else {
					parms = new TileParameters(xPos, yPos, "resource_stone_" + tileMapPos, world_h, world_w, world_scale, test);
					id = (int)World.callManager("post_createObjectFromTile", parms);
				}
				if(id != -1)
					this.objectsInTile.add(id);
			}
		}
		else if(tile_type == 1){
			// is a food tile without chance
			parms = new TileParameters(xPos, yPos, "food_apple_"+tileMapPos, world_h, world_w, world_scale, test);
			int id = (int)World.callManager("post_createObjectFromTile", parms);
			if(id != -1)
				this.objectsInTile.add(id);
		}
		else if(tile_type == 2){
			tileMapPos = World.getMapListSize()-1;
			if(tileMapPos < 0) tileMapPos = 0;
			// this means that this tile will have a hostile without chance
			parms = new TileParameters(xPos, yPos, "hostile_F_",  world_h, world_w, world_scale, test);
			int id = (int)World.callManager("post_createObjectFromTile", parms);
			System.out.println("hostile id added " + id);

			if(id != -1) {
				InteractableObject obj = (InteractableObject)World.callManager("get_object", id);
				World.addMap(obj.getTileMap(), id);
				this.objectsInTile.add(id);
			}
		}
		else if(tile_type == 3 && tileMapPos != -1){
			// is a food tile without chance
			parms = new TileParameters(xPos, yPos, "food_apple_" +tileMapPos, world_h, world_w, world_scale, test);
			int id = (int)World.callManager("post_createObjectFromTile", parms);
			if(id != -1)
				this.objectsInTile.add(id);
		}
		else if(tile_type == 4){
			int id;
			if(Util.random(100) < 50) {
				parms = new TileParameters(xPos, yPos, "resource_wood_" + tileMapPos, world_h, world_w, world_scale, test);
				id = (int)World.callManager("post_createObjectFromTile", parms);
			}else {
				parms = new TileParameters(xPos, yPos, "resource_stone_" + tileMapPos, world_h, world_w, world_scale, test);
				id = (int)World.callManager("post_createObjectFromTile", parms);
			}
			if(id != -1)
				this.objectsInTile.add(id);
		}else if(tile_type == 5){
			parms = new TileParameters(xPos, yPos, "resource_wood_" + tileMapPos, world_h, world_w, world_scale, test);
			int id = (int)World.callManager("post_createObjectFromTile", parms);
			this.objectsInTile.add(id);
		}else if(tile_type == 6){
			parms = new TileParameters(xPos, yPos, "resource_stone_" + tileMapPos, world_h, world_w, world_scale, test);
			int id = (int)World.callManager("post_createObjectFromTile", parms);
			this.objectsInTile.add(id);
		}
		// others later

	}

	// render
    public void render(Graphics2D g2d){
        for(int id : this.objectsInTile){
			InteractableObject obj = (InteractableObject)World.callManager("get_object", id);
        	obj.render(g2d);
		}
    }

	public void render(Graphics2D g2d, int x, int y){
		for(int id : this.objectsInTile){
			InteractableObject obj = (InteractableObject)World.callManager("get_object", id);
			obj.render(g2d,x,y);
		}
	}

    // update
    public void update(int seconds){
		for(int id : this.objectsInTile){
			InteractableObject obj = (InteractableObject)World.callManager("get_object", id);
			obj.update(seconds);
		}
    }

	// Getters and setters below
	public int getxPos() { return xPos; }

	public void setxPos(int xPos) { this.xPos = xPos; }

	public int getyPos() { return yPos; }

	public void setyPos(int yPos) { this.yPos = yPos; }

	public List<Integer> getEntitiesInTile() { return entitiesInTile; }
	public void addEntity(Entity e){
		for(int i = 0; i < this.entitiesInTile.size(); i++) {
			if (this.entitiesInTile.get(i) == e.getEntityID())
				return;
		}
		this.entitiesInTile.add(e.getEntityID());
	}
	// remove entity by ID
	public boolean removeEntity(int entityID){
		for(int i = 0; i < this.entitiesInTile.size(); i++){
			if(this.entitiesInTile.get(i) == entityID){
				entitiesInTile.remove(i);
				return true;
			}//
		}//
		return false;
	}//

	public void setEntitiesInTile(List<Integer> entitiesInTile) { this.entitiesInTile = entitiesInTile; }

	// extra object options, very similar to the entity logic
	public int addObject(int id){
		if(!this.objectsInTile.contains(id)){
			this.objectsInTile.add(id);
			return 1;
		}
		return -1;
	}//
	//
	public boolean removeObject(int objectID) {
		for (int i = 0; i < this.objectsInTile.size(); i++) {
			if (this.objectsInTile.get(i) == objectID) {
				this.objectsInTile.remove(i);
				return true;
			}
		}
		return false;
	}

	public List<Integer> getObjectsInTile() { return objectsInTile; }

	public void setObjectsInTile(List<Integer> objectsInTile) { this.objectsInTile = objectsInTile; }

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
		for(int id : this.objectsInTile){
			InteractableObject obj = (InteractableObject)World.callManager("get_object", id);
			line += "" + obj.getType() + " ";
		}
		return line;
	}

	public int getTile_type() {
		return tile_type;
	}

	public int getWorld_h() {
		return world_h;
	}

	public int getWorld_w() {
		return world_w;
	}

	public int getWorld_scale() {
		return world_scale;
	}
}
