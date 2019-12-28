package org.luna.logic.service;

import org.luna.core.util.ManagerCmd;

import java.util.List;
import java.awt.Graphics2D;

public interface Manager {

    public List<ManagerCmd> update();

    // some managers will use this but not all
    // - will play a larger part once following a specific entity is added
    public void render(Graphics2D g);

    // everything has an id
    // - so you can get anything from a manager if you have an id for it
    public Object getVar(int id);
    // More items will be added

}
