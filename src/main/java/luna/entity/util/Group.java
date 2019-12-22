package luna.entity.util;

import luna.entity.Entity;
import luna.util.Logger;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;
import luna.world.objects.item.Item;
import luna.world.util.ObjectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
* Groups will be how entities can work together. This will be the parent class:
*   - Subs: Parties, communities, cities etc.
* */

public class Group {
    private static int counter;
    protected List<Integer> entitiesInGroup;
    protected int leader;
    protected int[] basePos;
    protected int groupId;
    protected int groupSize = 0;

    public Group(){
        leader = -1;
        basePos = new int[]{-1,-1};
        this.groupId = counter;
        entitiesInGroup = new ArrayList<>();
        counter++;
    }//

    // make sure that it removes any entities that have died
    public void checkEntityStatus(){
        for(int i = 0; i < entitiesInGroup.size(); i++){
            Entity tmp = (Entity)World.callManager("get_entity", entitiesInGroup.get(i));
            if(!tmp.isAlive()){
                entitiesInGroup.remove(i);
                i--;
            }
        }//

        if(!entitiesInGroup.contains(leader)){
            if(entitiesInGroup.size() > 0){
                System.out.println("changing leader from " + leader + " to " + entitiesInGroup.get(0));
                leader = entitiesInGroup.get(0);
                World.callManager("post_setEntityFocus_leader",leader);
            }
        }// done
    }

    public void addEntity(int id){
        if(!entitiesInGroup.contains(id)){
            if(leader == -1){
                leader = id;
            }
            entitiesInGroup.add(id);
            groupSize++;
        }
    }// done

    // This will go through and call the get group request method
    // - it will check the entities current status and asses if it needs to get food or not,
    //   - if one or more entities need food the group will set a food task and gather food.
    // This method will be called by the leader and will set tasks
    public ArrayList<String> getGroupNeeds(){
        ArrayList<String> needs = new ArrayList<>();
        //
        for(int id : entitiesInGroup){
            Entity tmp = (Entity)World.callManager("get_entity", id);
            String task = tmp.getTaskNeed();
            if(task.length() > 0)
                needs.add(task);
        }
        return needs;
    }

    public void completeTask(){
        Entity leaderEntity = (Entity)World.callManager("get_entity", leader);
        int task = leaderEntity.getCurrentTask().getGoal();
        // eating goal
        if(task == 1) {
            InteractableObject obj = leaderEntity.getCurrentTask().getObject();
            distributeXp(leaderEntity.getCurrentTask().getXp());
            for(int id : entitiesInGroup){
                World.callManager("post_entityEat_" + id, obj);
            }
        }//
        // healing goal
        else if(task == 2){
            distributeXp(leaderEntity.getCurrentTask().getXp());
            for(int id : entitiesInGroup){
                World.callManager("post_entityRestoreHp", id);
            }
        }else if(task == 7){
            distributeXp(leaderEntity.getCurrentTask().getXp());
        }
        else if(task == 8){
            grantXp(leaderEntity.getCurrentTask().getXp());

            int[] newPos = new int[]{0,0};
            newPos[0] = leaderEntity.getCurrentTask().getTargetGPS()[0];
            newPos[1] = leaderEntity.getCurrentTask().getTargetGPS()[1];
            World.callManager("post_setGroupBase_" + newPos[0] + "_" + newPos[1], groupId);
            //
        }
        else if(task == 13){ // gather
            System.out.println("finished gather");
            Item tmp = (Item)World.callManager("post_harvestObject", leaderEntity.getCurrentTask().getObject().getObjectID());
            int result = (int)World.callManager("post_entityAddItem_" + leader, tmp);
            System.out.println("add item called " + result);
        }

        // end

        // other tasks added later
    }

    // on top of the entity that kills an enemy in the group, there is a party bonus as well that goes to everyone
    public void distributeXp(int xp){
        for(int id : entitiesInGroup){
            int xpAdd = (xp/3)+1; // 1 third of xp added, plus one to ensure xp added
            World.callManager("post_entityAddXp_" + xpAdd, id);
        }
    }

    // normal add to everyone
    public void grantXp(int xp){
        for(int id : entitiesInGroup){
            World.callManager("post_entityAddXp_" + xp, id);// xp add
        }
    }

    public void groupChangePosition(int pos){
        String logLine = "changing position to " + pos + " for:\n";
        for(int id : entitiesInGroup){
            logLine += id + " ";
            World.callManager("post_entitySetPosition_" + pos, id);
            if(pos != -1){
                World.callManager("post_entitySetSubYX_5_5", id);
                World.callManager("post_entitySetDirection_" + Util.stringToIntDirectionMap.get("down"), id);
            }
        }
        Logger log = (Logger)World.callManager("get_entityTaskLogger", leader);
        log.write(logLine);
    }

    public void setBasePos(int []pos){
        if(pos.length != 2){
            System.out.println("Error pos of length " + pos.length + " is too large!");
            return;
        }
        basePos[0] = pos[0];
        basePos[1] = pos[1];
    }

    public List<Integer> getEntitiesInGroup() {
        return entitiesInGroup;
    }

    public int getLeader() {
        return leader;
    }

    public void setLeader(int i){
        leader = i;
    }

    public int getGroupId() {
        return groupId;
    }

    public int[] getBasePos() {
        return basePos;
    }

    public int size(){
        return entitiesInGroup.size();
    }

    public void removeEntityFromList(int id){
        if(getEntitiesInGroup().contains(id)){
            for(int i = 0; i < entitiesInGroup.size(); i++){
                if(id == entitiesInGroup.get(i)){
                    entitiesInGroup.remove(i);
                    i--;
                }
            }
        }
    }// done

    // returns all held item ids within the group
    public List<Integer> getItemsInGroup(){
        ArrayList<Integer> items = new ArrayList<>();
        for(int id : entitiesInGroup){

            ArrayList<Integer> list = (ArrayList<Integer>) World.callManager("get_itemsOnPerson", id);
            items.addAll(list);
        }
        //
        return items;
    }
}
