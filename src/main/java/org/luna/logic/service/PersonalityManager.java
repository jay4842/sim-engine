package org.luna.logic.service;

import org.luna.core.util.ManagerCmd;

import java.awt.*;
import java.util.List;

// This guy will store all 16 types of personalities.
// - https://www.16personalities.com/personality-types#analysts
public class PersonalityManager implements Manager{

    public PersonalityManager(){
        // TODO
    }

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

    }

    @Override
    public boolean reset() {
        return false;
    }

    @Override
    public String getReportLine() {
        return null;
    }

    @Override
    public void databasePush() {

    }

    public void laodPersonalityFile(){
        // load json file containing all the personalities in the sim
        // TODO
    }

}
