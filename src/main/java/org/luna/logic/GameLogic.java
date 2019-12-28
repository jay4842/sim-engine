package org.luna.logic;

import java.awt.*;

// This guy will be where my game logic is held
public class GameLogic {

    MasterManager manager;

    public GameLogic(int HEIGHT, int WIDTH, int world_scale){
        System.out.println("Making game logic");
        manager = new MasterManager(HEIGHT, WIDTH, world_scale);
        // will add
    }

    public void update(){
        // TODO
        manager.update(0);
    }

    public void render(Graphics2D g){
        // TODO
        manager.render(0, g);
    }

    public void shutdown(){

    }
    //
}
