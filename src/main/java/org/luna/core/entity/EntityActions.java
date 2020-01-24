package org.luna.core.entity;

import java.util.List;
import java.awt.Graphics2D;
import org.luna.core.map.LunaMap;

public interface EntityActions {
    // TODO: define basic actions
    public String update(int step, int turnSize, LunaMap map);
    public void render(Graphics2D g);
    public void move(int direction, int step);
    public String makeReportLine();
    // TODO
    //public void attack(int entityID);
    //public void eat(int obj);
    public String makeStatusMessage(); // will be used for log reporting
    //public int dropItem(int itemPos); // drop an object from the inventory into the tiles list it is on

}
