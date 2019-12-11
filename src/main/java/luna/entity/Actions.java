package luna.entity;

import luna.world.objects.InteractableObject;

// first lets define some example functions
public interface Actions {
    public void move(int direction);
    public void attack(int entityID);
    public void eat(InteractableObject e);
    public String makeStatusMessage(); // will be used for log reporting
    public InteractableObject dropObject(int objPos); // drop an object from the inventory into the tiles list it is on

}