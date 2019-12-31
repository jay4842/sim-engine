package org.luna.logic;

import java.awt.*;

// This guy will be where my game logic is held
public class GameLogic {

    private MasterManager manager;
    private int step;

    int h, w, s;
    public GameLogic(int HEIGHT, int WIDTH, int world_scale){
        this.h = HEIGHT;
        this.w = WIDTH;
        this.s = world_scale;
        System.out.println("Making game logic");
        manager = new MasterManager(HEIGHT, WIDTH, world_scale);
        step = 0;
        // will add
    }

    public void update(){
        // TODO
        manager.update(step, 0);
        step++;

        if(step > (60) * 10 ||manager.reset()){ // reset every minute
            manager.shutdown();
            manager.resetEntities();
            try{
                Thread.sleep(10);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            step = 0;
        }
    }

    public void render(Graphics2D g){
        // TODO
        manager.render(0, step, g);
    }

    public void registerInput(){

    }

    public void shutdown(){

    }
    //
}
