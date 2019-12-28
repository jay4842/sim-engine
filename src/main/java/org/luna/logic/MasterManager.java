package org.luna.logic;

import org.luna.core.util.ManagerCmd;
import org.luna.logic.service.*;

import java.util.List;
import java.awt.Graphics2D;


// hold all of the other managers
public class MasterManager implements Manager{
    // TODO
    private WorldManager worldManager;
    private ItemManager itemManager;

    public MasterManager(){
        worldManager = new WorldManager();
        itemManager = new ItemManager();
    }

    @Override
    public List<ManagerCmd> update() {
        worldManager.update();
        itemManager.update();

        return null;
    }

    @Override
    public void render(Graphics2D g) {
        worldManager.render(g);
    }

    @Override
    public Object getVar(int id) {
        return null;
    }
}
