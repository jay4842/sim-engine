package luna.entity.util;

import luna.entity.Entity;
import luna.util.Logger;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;
import luna.world.util.ObjectManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskUtil {

    private String [] taskTypes;
    private String [] buildingTypes;

    TaskUtil(){
        taskTypes = new String[]{"none", "food", "rest", "move", "wander",
                "attack", "train", "hostile","build", "group", "interact",
                "breed", "find", "gather"};
        buildingTypes = new String[]{"camp"};
    }//

    public String[] getTaskTypes(){return taskTypes;}
    public String[] getBuildingTypes(){return buildingTypes;}

    public int getTaskType(String type){
        for(int i = 0; i < taskTypes.length; i++){
            if((type.split("_")[0]).contains(taskTypes[i])){
                return i;
            }
        }
        return 0;
    }

    // TODO: add gather task
    public TaskRef makeTask(TaskRef ref, List<List<Tile>> tileMap, int seconds){
        Logger taskLogger = (Logger)World.callManager("get_entityTaskLogger", ref.getEntityID());
        Entity taskEntity = (Entity)World.callManager("get_entity", ref.getEntityID());
        Group taskGroup = null;

        if(taskEntity.getGroupId() != -1)
            taskGroup = (Group)World.callManager("get_group", taskEntity.getGroupId());
        taskLogger.write("----------- Making Task " + ref.getRefId() + ", Goal = " + getTaskTypes()[ref.getGoal()] + " -----------");
        if(ref.getTaskType().equals("food") || ref.getTaskType().equals("hostile") || ref.getTaskType().equals("gather") || ref.getTaskType().equals("build") ){//|| (taskEntity.getGroupId() != -1 && taskGroup.getBasePos()[0] != -1 && ref.getTaskType().equals("rest")) ) { // more later
            String[] split = ref.getNotes().split("_");
            if (split.length < 3)
                ref.setTargetGPS(findTile(ref, tileMap));
            else {
                ref.setTargetGPS(new int[]{Integer.parseInt(split[split.length - 4]), Integer.parseInt(split[split.length - 3]), Integer.parseInt(split[split.length - 2]), Integer.parseInt(split[split.length - 1])});
            }
            taskLogger.writeNoTimestamp("Moving To Target");
            taskLogger.writeNoTimestamp("[" + ref.getStartGPS()[0] +" " + ref.getStartGPS()[1] + "] -> [" + ref.getTargetGPS()[0] + " " + ref.getTargetGPS()[1] +  "]");
            taskLogger.writeNoTimestamp("Moves Found");
            ref.setMoves(makePath(ref,tileMap));
            for(List<Integer> move : ref.getMoves()){
                taskLogger.writeNoTimestamp("[" + move.get(0) + " " + move.get(1) + "]");
            }
            // show the map for the logger
            String buffer = "";
            for(int i = 0; i < tileMap.size(); i++){
                for(int j = 0; j < tileMap.get(i).size(); j++){
                    if(ref.getStartGPS()[0] == i && ref.getStartGPS()[1] == j)
                        buffer += "A ";
                    else if(ref.getTargetGPS()[0] == i && ref.getTargetGPS()[1] == j)
                        buffer += "B ";
                    else buffer += "_ ";
                }
                taskLogger.writeNoTimestamp(buffer);
                buffer = "";
            }
        }else if(ref.getTaskType().contains("move")) {
            // the target will be passed in goal
            String[] split = ref.getNotes().split("_");
            if (split.length < 2)
                return null; // error present
            ref.setTargetGPS(new int[]{Integer.parseInt(split[1]), Integer.parseInt(split[2]), ref.getStartGPS()[2], -1});
            taskLogger.writeNoTimestamp("Moving to target: [" + ref.getTargetGPS()[0] + " " + ref.getTargetGPS()[1] + "]");
            taskLogger.writeNoTimestamp("Moving To Target");
            taskLogger.writeNoTimestamp("[" + ref.getStartGPS()[0] +" " + ref.getStartGPS()[1] + "] -> [" + ref.getTargetGPS()[0] + " " + ref.getTargetGPS()[1] +  "]");
            taskLogger.writeNoTimestamp("Moves Found");
            ref.setMoves(makePath(ref,tileMap));
            for(List<Integer> move : ref.getMoves()){
                taskLogger.writeNoTimestamp("[" + move.get(0) + " " + move.get(1) + "]");
            }
            // show the map for the logger
            String buffer = "";
            for(int i = 0; i < tileMap.size(); i++){
                for(int j = 0; j < tileMap.get(i).size(); j++){
                    if(ref.getStartGPS()[0] == i && ref.getStartGPS()[1] == j)
                        buffer += "A ";
                    else if(ref.getTargetGPS()[0] == i && ref.getTargetGPS()[1] == j)
                        buffer += "B ";
                    else buffer += "_ ";
                }
                taskLogger.writeNoTimestamp(buffer);
                buffer = "";
            }
        }else{
            ref.setTargetGPS(new int[]{-1,-1,taskEntity.getPosition(),-1});
        }
        if((taskEntity.getGroupId() == -1 && ref.getTaskType().contains("rest")) || ref.getTaskType().contains("interact") ||
           ref.getTaskType().contains("none") || ref.getTaskType().contains("wander") || ref.getTaskType().contains("find")){
            ref.setTargetTime(seconds + 5); // wait five seconds
        }
        taskLogger.writeNoTimestamp("need  = " + ref.getNotes());
        taskLogger.writeNoTimestamp("Target TileMapPos = " + ref.getTargetGPS()[2]);
        taskLogger.writeNoTimestamp("Target object ID  = " + ref.getTargetGPS()[3]);
        taskLogger.writeNoTimestamp("-------------------------------------------------------------------------\n");
        //System.out.println("tileMapPos = " + this.targetMapPos);
        return ref;
    }
    // end of MakeTask

    // bad practice but putting parameter specific values here, I don't want these to be passed by the function call
    protected int kernel = 3; // protected, child classes may change ho this works
    protected int maxFail = 100;
    protected int tileType = 0;
    String objectType = "None";
    // Find a tile by looking at the adjacent tiles, then randomly looking around the map
    // - This should not be too difficult, also it may depend on the entities search range later
    public int [] findTile(TaskRef ref, List<List<Tile>> tileMap){
        Logger taskLogger = (Logger)World.callManager("get_entityTaskLogger", ref.getEntityID());
        int fails = 0;
        int ySize = tileMap.size();
        int xSize = tileMap.get(0).size();
        int targetMapPos = -1;
        int objectID = -1;

        String target = getTaskTypes()[ref.getGoal()];
        if(target.equals("gather")){
            target = ref.getNotes().split("_")[1]; // gather_<resource name>
        }

        taskLogger.writeNoTimestamp("Looking for -> " + target);
        /* If we get here we need to look at other kernels */
        kernel = 3; // make it a little easier to find something
        int xPos = ref.getStartGPS()[1];
        int yPos = ref.getStartGPS()[0];

        while(fails <= maxFail){
            if(fails > 10 && kernel < 11 && fails % 10 == 0){
                kernel+=2;
            }

            for(int i = -1; i < kernel-1; i++){
                int k_y = xPos + i; // y
                for(int j = -1; j < kernel-1; j++){
                    int k_x = yPos + j; // x
                    // make sure we are in bounds
                    if(k_y >= 0 && k_x >= 0 && k_y <= ySize-1 && k_x <= xSize-1){
                        // loop through the objects in the tile and see if there is an object with the target type
                        for(int id : tileMap.get(k_y).get(k_x).getObjectsInTile()){
                            InteractableObject obj = (InteractableObject) World.callManager("get_object", id);
                            if(obj.getType().contains(target) && obj.isActive()){
                                // note some entities will have type restrictions for targets, child classes will define the logic
                                if(ref.getGoal() == 7 || ref.getGoal() == 1 || ref.getGoal() == 12 || ref.getGoal() == 13) {
                                    String[] split = obj.getType().split("_");
                                    //System.out.println(Integer.parseInt(split[split.length - 1]));
                                    targetMapPos = Integer.parseInt(split[split.length - 2]);
                                    objectID = Integer.parseInt(split[split.length - 1]);
                                    taskLogger.writeNoTimestamp("Object type -> " + obj.getType());
                                    taskLogger.writeNoTimestamp("Target position -> " + targetMapPos);
                                    taskLogger.writeNoTimestamp("Object ID -> " + objectID);
                                }
                                //
                                return new int[]{k_y,k_x, targetMapPos, objectID};
                            }
                        }
                    }
                }
            }
            // we didn't find one
            fails ++;
            xPos = Util.random(xSize);
            yPos = Util.random(ySize);
        }
        // using the
        // default
        taskLogger.writeNoTimestamp("Find tile timeout");
        return new int[]{-1, -1, -1, -1};
    }// end of find tile

    // Path find using simple distance calculation
    public double calCost(int[] pos, int[] target){
        double vert = Math.pow(target[0] - pos[0], 2);
        double horz = Math.pow(target[1] - pos[1], 2);
        double sum = horz + vert;
        //System.out.println("sqrt(" + vert + " + " + horz + ") = " + result);
        return Math.sqrt(sum);
    }
    // Find
    public List<List<Integer>> makePath(TaskRef ref,List<List<Tile>> tileMap){
        Logger taskLogger = (Logger)World.callManager("get_entityTaskLogger", ref.getEntityID());

        taskLogger.writeNoTimestamp("Making a call to make moves");
        List<List<Integer>> moves = Collections.synchronizedList(new ArrayList<List<Integer>>());
        int [] current_pos = {ref.getStartGPS()[0], ref.getStartGPS()[1]};
        int [] targetTile = {ref.getTargetGPS()[0], ref.getTargetGPS()[1]};
        int turnsSpent = 0; // if this gets to 500 it will timeout
        int ySize = tileMap.size();
        int xSize = tileMap.get(0).size();
        int kernel = 3; // needs to stay three
        // Finding path steps:
        do{
            List<List<Integer>> availableMoves = Collections.synchronizedList(new ArrayList<List<Integer>>());
            // first make the list of possible moves
            for(int i = -1; i < kernel-1; i++) {
                int k_y = current_pos[0] + i; // y
                for (int j = -1; j < kernel - 1; j++) {
                    int k_x = current_pos[1] + j; // x
                    // make sure we are in bounds
                    if (k_y >= 0 && k_x >= 0 && k_y <= ySize - 1 && k_x <= xSize - 1) {
                        ArrayList<Integer> newMove = new ArrayList<>();
                        newMove.add(k_y);
                        newMove.add(k_x);
                        availableMoves.add(newMove);
                    }
                }
            }//
            // check the current kernel for the best move
            double bestDistance = 100000.0;
            ArrayList<Integer> nextMove = new ArrayList<>();
            nextMove.add(-1);
            nextMove.add(-1);
            for(List<Integer> move : availableMoves){
                double dist = calCost(new int[]{move.get(0), move.get(1)}, targetTile);
                if(dist < bestDistance){
                    bestDistance = dist;
                    nextMove.set(0, move.get(0));
                    nextMove.set(1, move.get(1));
                }
            }
            //
            current_pos[0] = nextMove.get(0);
            current_pos[1] = nextMove.get(1);
            moves.add(nextMove);
            turnsSpent++;
        }while((current_pos[0] != targetTile[0] || current_pos[1] != targetTile[1]) && turnsSpent < 500);
        if(turnsSpent >= 500)
            taskLogger.write("Broke out of pathfind due to timeout");

        return moves;
    }

    public int makePriority(int goal){
        switch (goal){
            case 0: // none
            case 4: // wander
                return 0;
            case 10:// interact
                return 1;
            case 7: // hostile
            case 12: // find
                return 2;
            case 8: // build
            case 13:// gather
                return 3;
            case 1: // food
            case 2: // rest
                return 4;
            case 3: // move
                return 5;
            default: return 0;
        }
    }

    // reaching a tile does not always mean that the task is done
    public boolean targetTileReached(int []tile, TaskRef ref){
        return tile[0] == ref.getTargetGPS()[0] && tile[1] == ref.getTargetGPS()[1];
    }

    boolean isTaskFinished(TaskRef ref, int[] currTile, int seconds, int pos){
        if(ref.getTargetGPS()[0] != -1 && !ref.getTaskType().equals("hostile")) { // there are other things we need to check
            //System.out.println("[" + currTile[0] + " " + currTile[1] + "] -> [" + ref.getTargetGPS()[0] + " " + ref.getTargetGPS()[1] + "]");
            //System.out.println("Pos? " + (pos == ref.getTargetGPS()[3]));
            return currTile[0] == ref.getTargetGPS()[0] && currTile[1] == ref.getTargetGPS()[1];
        }
        if(ref.getTargetTime() > 0)
            return (seconds >= ref.getTargetTime());

        if(ref.getTaskType().equals("hostile") && ref.getTargetGPS()[0] != -1){
            //System.out.println("goal of 7 checking");
            // find it another way
            //System.out.println("pos provided -> " + pos);
            if(pos > -1){
                int tmpId = World.subMaps.get(pos).getObjectID();
                InteractableObject obj = (InteractableObject) World.callManager("get_object", tmpId);
                //System.out.println("tmpID -> " + tmpId);
                //System.out.println(World.tileMap.get(targetTile[0]).get(targetTile[1]).getObjectsInTile().get(tmpId).isActive());
                if(World.tileMap.get(ref.getTargetGPS()[0]).get(ref.getTargetGPS()[1]).getObjectsInTile().size() > 0)
                    return !(obj.isActive()); // we want the oppisite of this guy
                else
                    return true; // if we get to this that means there is an error with with the objects, so lets forget about it
            }
        }

        // Notes will always contain two words for find, find_<object Type>

        return false;
    }

    // after a task is made sometimes we can get an invalid task, like a hostile with a target of -1 -1, and same goes for food
    public boolean isValid(TaskRef ref){
        if(ref.getTaskType().equals("food")   || ref.getTaskType().equals("hostile") ||
           ref.getTaskType().equals("gather") || ref.getTaskType().equals("build")){
            return ref.getTargetGPS()[0] != -1;
        }else if(ref.getTaskType().equals("rest")){
            Entity taskEntity = (Entity)World.callManager("get_entity", ref.getEntityID());
            if(taskEntity.getGroupId() != -1) {
                Group g = (Group) World.callManager("get_group", taskEntity.getGroupId());
                return (g.getBasePos()[0] != ref.getTargetGPS()[0] && g.getBasePos()[1] != ref.getTargetGPS()[1] && g.getBasePos()[2] != ref.getTargetGPS()[2]);
            }
        }
        return true;
    }

    public boolean timeExpired(TaskRef ref, int seconds){
        return seconds >= ref.getTargetTime();
    }
}
