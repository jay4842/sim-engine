package luna.entity.util;

import luna.entity.Entity;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;

// Some methods that can be called by something
public class EntityUtil {

    public EntityUtil(){

    }

    //
    public String getTaskRequest(Entity e){
        // hunger check first
        if(e.isHungry() && e.getCurrentTask().getGoal() != -1){
            return "Hungry";
        }
        // additional items
        if((e.getFocus().equals("fighter") || e.getFocus().equals("nomad")) && e.getHp() > e.getMax_hp()*.50 &&
                             Util.random(100) > 75 && e.notWaitingForHunt() && e.getCurrentTask().getGoal() != 7){
            return "Hunt";
        }

        if(e.getHp() <= e.getMax_hp()*.50 && e.getCurrentTask().getGoal() != 2 && e.getCurrentTask().getGoal() != 1)
            return "Heal";

        return "";
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
