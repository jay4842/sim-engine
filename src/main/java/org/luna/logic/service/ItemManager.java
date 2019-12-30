package org.luna.logic.service;

import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.List;

// Handle all the items created in the game
public class ItemManager implements Manager{

    public ItemManager(){

    }

    @Override
    public List<ManagerCmd> update(int step, int x) {

        return null;
    }

    @Override
    public void render(int x, Graphics2D g) {

    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown(){

    }
}
