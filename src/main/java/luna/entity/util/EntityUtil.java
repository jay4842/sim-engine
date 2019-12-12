package luna.entity.util;

import luna.entity.Entity;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;

// Some methods that can be called by something
public class EntityUtil {

    public EntityUtil(){

    }

    // Will add others soon
    public String getTaskRequest(Entity e){
        // hunger check first
        if(e.isHungry()){
            return "food";
        }
        // additional items
        if((e.getFocus().equals("fighter") || e.getFocus().equals("nomad")) && e.getHp() > e.getMax_hp()*.50 &&
                Util.random(100) > 85 && e.notWaitingForHunt()){
            return "hostile";
        }

        if(e.getHp() <= e.getMax_hp()*.50)
            return "rest";

        return "none";
    }

    public int faceOpposite(int dir){
        switch(dir){
            case 0: // left
                return 1;
            case 1:// right
                return 0;
            case 2:// up
                return 3;
            case 3:// down
                return 2;
            default:
                return 0;
        }
    }//
}
