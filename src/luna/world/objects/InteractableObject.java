package luna.world.objects;

import luna.util.Tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

// Throuought the world entites will be able to interact with these types of objects
public class InteractableObject{
	static int world_h=-1, world_w=-1, world_scale=-1;
	private int objectID;
	protected int xPos, yPos;
	protected int currTileX, currTileY;
	protected String type;
	protected boolean active = false;
	protected boolean destroyed = false; // if this object is destroyed it will be removed from the object list

	// type:
	// A string string consisting of two numbers separated by a '.' To make indexing and referencing simple
	// what makes up an object that can be interacted with at the base
	public InteractableObject(int xPos, int yPos, String type, int objectID,int world_h, int world_w, int world_scale) {
		this.objectID = objectID;
		// base is location, and type
		this.xPos = xPos;
		this.yPos = yPos;
		this.type = type;
		// if one of them is -1 then they all should be
		if (this.world_h == -1) {
			this.world_h = world_h;
			this.world_w = world_w;
			this.world_scale = world_scale;
		}

		this.currTileX = xPos / world_scale;
		this.currTileY = yPos / world_scale;
		//
	}
	// base render
	public void render(Graphics2D g2d) {
		g2d.setColor(Color.green);
		g2d.fillRect(xPos, yPos, 15,15);
	}
	
	public void update(int seconds) {
		// this will remove itself
	}

	public int getObjectID() {
		return objectID;
	}

	public void setObjectID(int objectID) {
		this.objectID = objectID;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
}
