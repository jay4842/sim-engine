package org.luna.logic;

import org.luna.core.util.ManagerCmd;
import org.luna.logic.service.*;

import java.util.List;
import java.awt.Graphics2D;


// hold all of the other managers
public class MasterManager implements Manager{
    private WorldManager worldManager;
    private int simId;
    MasterManager(int HEIGHT, int WIDTH, int world_scale, int turnStep, int simId){
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
        // now we should send files to the headPi for intake
        // - send resource

        // send item Ref info
        // send personality Ref info
        // send object Ref info
        // send Sim info
        // send entity info

    }

    public boolean reset(){
        return worldManager.reset();
    }

    void resetWorld(){
        simId++;
        worldManager.resetEntityManager(simId);
        worldManager.resetMaps();
    }

    public String getReportLine(int step){
        return worldManager.getReportLine(step);
    }


    public void databasePush(){
        worldManager.databasePush();
    }
}
