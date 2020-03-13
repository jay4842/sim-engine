package org.luna.logic;

import org.luna.core.util.ManagerCmd;
import org.luna.logic.service.*;

import java.util.List;
import java.awt.Graphics2D;


// hold all of the other managers
public class MasterManager implements Manager{
    private WorldManager worldManager;
    private int simId;
    public MasterManager(int HEIGHT, int WIDTH, int world_scale, int turnStep, int simId){
        System.out.println("making master manager");
        worldManager = new WorldManager(HEIGHT, WIDTH, world_scale, turnStep, simId);
    }

    @Override
    public List<ManagerCmd> update(int step, int x) {
        List<ManagerCmd> worldCmds = worldManager.update(step, x);
        // parse worldCmds
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

    public void resetWorld(){
        simId++;
        worldManager.resetEntityManager(simId);
        worldManager.resetMaps();
    }

    public String getReportLine(){
        return worldManager.getReportLine();
    }

    public void databasePush(){
        worldManager.databasePush();
    }
}
