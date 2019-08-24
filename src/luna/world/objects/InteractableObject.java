package luna.world.objects;

import java.awt.Color;
import java.awt.Graphics2D;

// Throuought the world entites will be able to interact with these types of objects
public class InteractableObject {
	protected int xPos, yPos, type;
	// what makes up an object that can be interacted with at the base
	public InteractableObject(int xPos, int yPos, int type) {
		// base is location, and type
		this.xPos = xPos;
		this.yPos = yPos;
		this.type = type;
		//
	}
	// TODO define rest of scope
	
	public void render(Graphics2D g2d) {
		// TODO add images
		g2d.setColor(Color.green);
		g2d.fillRect(xPos, yPos, 15,15);
	}
	
	public void update() {
		// TODO define scope
	}
}
