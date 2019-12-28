package org.luna.logic.service;

import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.List;

// Hold all of the maps in the world
public class WorldManager implements Manager{

    // sense objects belong to the world, it will belong to the world manager as well
    private ObjectManager objectManager;
    private EntityManager entityManager;

    public WorldManager(){
        objectManager = new ObjectManager();
        entityManager = new EntityManager();
    }

    @Override
    public List<ManagerCmd> update() {
        entityManager.update();
        objectManager.update();
        // TODO
        return null;
    }

    @Override
    public void render(Graphics2D g) {
        entityManager.render(g);
        objectManager.render(g);
    }

    @Override
    public Object getVar(int id) {
        return null;
    }
}
