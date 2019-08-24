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
	private List<Entity> entitesInTile = Collections.synchronizedList(new ArrayList<Entity>());
	private List<InteractableObject> objectsInTile = Collections.synchronizedList(new ArrayList<InteractableObject>()) ;
	
	public Tile(int xPos, int yPos) {
		
	}

}
