package org.luna.core.object.food;

import org.luna.core.object.WorldObject;

import java.awt.*;

public class FoodBase extends WorldObject {

    private String foodType;

    public FoodBase(int[] gps, int listId, int size){
        super(listId, gps, 1, size);
        foodType = "base_food";
    }

    @Override
    public void render(int x, Graphics2D g) {
        super.render(x, g);
        g.setColor(Color.red);
        g.fillRect(getGps()[1], getGps()[0], getSize(), getSize());
    }

    public Object getDescription(){
        return foodType;
    }
}
