package luna.entity.util;

import luna.entity.Entity;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;

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

    public Group(int groupId){
        leader = -1;
        basePos = new int[]{-1,-1};
        this.groupId = groupId;
        entitiesInGroup = new ArrayList<>();
        counter++;
    }//

    // make sure that it removes any entities that have died
    public void checkEntityStatus(){
        for(int i = 0; i < entitiesInGroup.size(); i++){
            if(!EntityManager.entities.get(entitiesInGroup.get(i)).isAlive()){
                entitiesInGroup.remove(i);
                i--;
            }
        }//

        if(!entitiesInGroup.contains(leader)){
            if(entitiesInGroup.size() > 0){
                System.out.println("changing leader from " + leader + " to " + entitiesInGroup.get(0));
                leader = entitiesInGroup.get(0);
                EntityManager.entities.get(leader).setFocus("leader");
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
            String task = EntityManager.entities.get(id).getTaskNeed();
            if(task.length() > 0)
                needs.add(task);
        }
        return needs;
    }

    public void completeTask(){
        int task = EntityManager.entities.get(leader).getCurrentTask().getGoal();
        // eating goal
        if(task == 1) {
            InteractableObject obj = EntityManager.entities.get(leader).getCurrentTask().getObject();

            for(int id : entitiesInGroup){
                EntityManager.entities.get(id).eat(obj);
            }
        }//
        // healing goal
        else if(task == 2){
            for(int id : entitiesInGroup){
                EntityManager.entities.get(id).setHp(EntityManager.entities.get(id).getMax_hp());
            }
        }// end

        // other tasks added later
    }

    // on top of the entity that kills an enemy in the group, there is a party bonus as well that goes to everyone
    public void distributeXp(int xp){
        for(int id : entitiesInGroup){
            EntityManager.entities.get(id).addXp((int)(xp/3)+1); // 1 third of xp added, plus one to ensure xp added
        }
    }

    // normal add to everyone
    public void grantXp(int xp){
        for(int id : entitiesInGroup){
            EntityManager.entities.get(id).addXp(xp); // 1 third of xp added, plus one to ensure xp added
        }
    }

    public void groupChangePosition(int pos){
        String logLine = "changing position to " + pos + " for:\n";
        for(int id : entitiesInGroup){
            logLine += id + " ";
            EntityManager.entities.get(id).setPosition(pos);
            if(pos != -1){
                EntityManager.entities.get(id).setSubX(5);
                EntityManager.entities.get(id).setSubY(5);
                EntityManager.entities.get(id).setDirection(Util.stringToIntDirectionMap.get("down"));
            }
        }
        EntityManager.entities.get(leader).getTaskLogger().write(logLine);
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
}
