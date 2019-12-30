package org.luna.core.entity;

import java.util.List;
import java.awt.Graphics2D;
import org.luna.core.map.LunaMap;

public interface EntityActions {
    // TODO: define basic actions
    public List<List<Integer[]>> update(int step, LunaMap map, List<Entity> entityList, List<List<Integer[]>>entityRef);
    public void render(Graphics2D g);
    public void move(int direction);
    // TODO
    //public void attack(int entityID);
    //public void eat(int obj);
    //public String makeStatusMessage(); // will be used for log reporting
    //public int dropItem(int itemPos); // drop an object from the inventory into the tiles list it is on

}
