package org.luna.core.entity;

import java.util.List;
import java.awt.Graphics2D;

import org.luna.core.item.Item;
import org.luna.core.map.LunaMap;

public interface EntityActions {
    // TODO: define basic actions
    List<String> update(int step, int turnSize, LunaMap map, int daySize);
    void render(Graphics2D g);
    void move(int direction, int step);
    String makeReportLine();
    String addItem(Item item);
    int dropItem(int invIdx); // drop the item creating an item world object by returning the unique id
    int destroyItem(int invIdx); // destroy an item from the item manager by using unique id
    public String makeStatusMessage(); // will be used for log reporting
    public boolean replicate();
    String interact(Entity e);
    float receiveInteraction(float f);
    // TODO
    //public void attack(int entityID);


}
