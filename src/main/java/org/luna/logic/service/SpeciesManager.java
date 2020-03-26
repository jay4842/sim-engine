package org.luna.logic.service;

import org.luna.core.util.ManagerCmd;

import java.awt.*;
import java.util.List;

// Will belong to the entity manager
// Will manage/catalog entities into separate species
public class SpeciesManager implements Manager{

    // TODO: need to define a species
    //  - what makes up a species?
    //  - how does it integrate with entity?

    @Override
    public List<ManagerCmd> update(int step, int x) {
        return null;
    }

    @Override
    public void render(int x, int step, Graphics2D g) {

    }

    @Override
    public Object getVar(int id) {
        return null;
    }

    @Override
    public void shutdown() {
        System.out.println("shutting down species manager");
    }

    @Override
    public boolean reset() {
        return false;
    }

    @Override
    public String getReportLine(int step) {
        return null;
    }

    @Override
    public void databasePush() {
        // TODO: prep files to send to PI
    }
}
