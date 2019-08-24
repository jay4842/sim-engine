package luna.world;

import luna.entity.Entity;

import java.awt.*;
import java.util.ArrayList;

// This guy will just hold all of our entities
public class World {
	/*
	 * Still working out how I want the map to be handled
	 * */
    public static ArrayList<Entity> entities = new ArrayList<Entity>();

    //
    int width, height, world_scale;
    public World(int width, int height, int world_scale){
        this.width = width;
        this.height = height;
        this.world_scale = world_scale;
        for(int i = 0; i < 1; i++) {
            int x = (int)(Math.random()*200)+10;
            int y = (int)(Math.random()*200)+10;
            Color c = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
            entities.add(new Entity(x,y,this.width,this.height,world_scale,c));
        }
    }//

    public void update(){

        // update entities
        for(Entity e : entities){
            e.update();
        }
    }
    //
    public void render(Graphics2D g){
        // render entities
        for(Entity e : entities){
            e.render(g);
        }

    }//
}
