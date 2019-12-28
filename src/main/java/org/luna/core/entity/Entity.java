package org.luna.core.entity;

import org.luna.core.util.State;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity implements EntityActions, State {
    static int counter = 0;
    private int id;
    private int type;
    private int groupID;
    private int[] gps;
    private int[] stats;
    private TaskRef task;
    private Personality personality;
    private List<Bond> bondList;
    private List<Integer> inventory;

    public Entity(){
        id = counter;
        counter++;
        bondList = new ArrayList<>();
        inventory = new ArrayList<>(5);
    }

    public void update(){

    }

    public void render(Graphics2D g){

    }

    // state interface
    @Override
    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<>();
        state.put("GPS", gps);
        state.put("TASK", task);
        state.put("STATS", stats);
        state.put("ITEMS", inventory.toArray());
        state.put("BONDS", bondList.toArray());
        return state;
    }

    @Override
    public void updateState() {

    }
}
