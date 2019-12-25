package luna.world.util;

import luna.main.Game;
import luna.world.World;

public class ObjectParameters {
    int xPos;
    int yPos;
    String type;
    int objectID;
    int world_h;
    int world_w;
    int world_scale;
    boolean test;

    public ObjectParameters(int xPos, int yPos, String type, int world_h, int world_w, int world_scale){
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
        this.world_h = world_h;
        this.world_w = world_w;
        this.world_scale = world_scale;
        this.test = false;
    }

    public ObjectParameters(int xPos, int yPos, String type){
        this.type = type;
        this.xPos = xPos;
        this.yPos = yPos;
        this.world_h = Game.HEIGHT;
        this.world_w = Game.WIDTH;
        this.world_scale = Game.world_scale;
        this.test = false;
    }

    public ObjectParameters(int xPos, int yPos, String type, boolean test){
        this.type = type;
        this.xPos = xPos;
        this.yPos = yPos;
        this.world_h = Game.HEIGHT;
        this.world_w = Game.WIDTH;
        this.world_scale = Game.world_scale;
        this.test = test;
    }
}
