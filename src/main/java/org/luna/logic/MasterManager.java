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

    public MasterManager(int HEIGHT, int WIDTH, int world_scale, int turnStep){
        System.out.println("making master manager");
        worldManager = new WorldManager(HEIGHT, WIDTH, world_scale, turnStep);
        itemManager = new ItemManager();
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        worldManager.update(step, x);
        itemManager.update(step, x);

        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {
        worldManager.render(x, step, g);
    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){
        worldManager.shutdown();
    }

    public boolean reset(){
        return worldManager.reset();
    }

    public void resetEntities(){
        worldManager.resetEntityManager();
    }

    public String getReportLine(){
        return worldManager.getReportLine();
    }

    public void databasePush(){
        worldManager.databasePush();
    }
}
