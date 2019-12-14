package luna.entity.util;

import luna.entity.Entity;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;

import java.util.ArrayList;
import java.util.List;

// Some methods that can be called by something
public class EntityUtil {

    private static String[] jobs = {"nomad", "leader", "fighter", "crafter"};
    public EntityUtil(){

    }

    // Will add others soon
    public String getTaskRequest(Entity e){
        // hunger check first
        if(e.isHungry()){
            return "food";
        }

        if(e.getHp() <= e.getMax_hp()*.50)
            return "rest";

        // TODO: define task build_XXXX
        //if(e.getTaskWaitTimer() <= 0 && e.getFocus().contains("crafter") && e.getGroupId() != -1 && World.entityManager.groups.get(e.getGroupId()).getBasePos()[0] == -1)
        //    return "build_camp";

        // additional items
        if(e.getTaskWaitTimer() <= 0 && (e.getFocus().contains("fighter") || e.getFocus().contains("nomad") || e.getFocus().contains("leader")) &&
                e.getHp() > e.getMax_hp()*.50 && Util.random(100) > 95 && e.notWaitingForHunt()){
            return "hostile";
        }

        if(e.getTaskWaitTimer() <= 0 && Util.random(100) > 95 && e.getInteractTimer() <= 0 && e.getGroupId() == -1 &&
                (e.taskQueueEmpty() || (e.getCurrentTask().getTaskType().equals("wander") || e.getCurrentTask().getTaskType().equals("none"))))
            return "interact";

        if(e.taskQueueEmpty())
            return "wander";


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

    public static String[] getJobs() {
        return jobs;
    }
}
