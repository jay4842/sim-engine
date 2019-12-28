package org.luna.logic.service;

import org.luna.core.util.ManagerCmd;

import java.awt.Graphics2D;
import java.util.List;

// Hold and manager all the entities in the game
// - note: entities will be
public class EntityManager implements Manager {

    public EntityManager(){

    }

    @Override
    public List<ManagerCmd> update() {

        return null;
    }

    @Override
    public void render(Graphics2D g) {

    }

    @Override
    public Object getVar(int id) {
        return null;
    }
}
