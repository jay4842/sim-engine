package luna.entity.util;

import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskUtil {

    private String [] taskTypes;
    private String [] buildingTypes;

    TaskUtil(){
        taskTypes = new String[]{"none", "food", "rest", "move", "wander",
                "attack", "train", "hostile","base", "group",
                "breed"};
        buildingTypes = new String[]{"camp"};
    }//

    public String[] getTaskTypes(){return taskTypes;}
    public String[] getBuildingTypes(){return buildingTypes;}


    //TODO: finish this guy
    public TaskRef makeTask(TaskRef ref, List<List<Tile>> tileMap, int seconds){
        ref.getLogger().write("----------- Making Task, Goal = " + getTaskTypes()[ref.getGoal()] + " -----------");
        if(ref.getGoal() == 1 || ref.getGoal() == 3 || ref.getGoal() == 7) { // more later
            ref.setTargetGPS(findTile(ref, tileMap));
            ref.getLogger().writeNoTimestamp("Moving To Target");
            ref.getLogger().writeNoTimestamp("[" + ref.getStartGPS()[0] +" " + ref.getStartGPS()[1] + "] -> [" + ref.getTargetGPS()[0] + " " + ref.getTargetGPS()[1] +  "]");
            ref.getLogger().writeNoTimestamp("Moves Found");
            ref.setMoves(makePath(ref,tileMap));
            for(List<Integer> move : ref.getMoves()){
                ref.getLogger().writeNoTimestamp("[" + move.get(0) + " " + move.get(1) + "]");
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
                ref.getLogger().writeNoTimestamp(buffer);
                buffer = "";
            }
        }else{
            ref.setTargetGPS(new int[]{-1,-1,-1,-1});
        }
        if(ref.getGoal() == 2){
            ref.setTargetTime(seconds + 5); // wait ten seconds
        }
        ref.getLogger().writeNoTimestamp("Target TileMapPos = " + ref.getTargetGPS()[2]);
        ref.getLogger().writeNoTimestamp("Target object ID  = " + ref.getTargetGPS()[3]);
        ref.getLogger().writeNoTimestamp("-------------------------------------------------------------------------\n");
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
        int fails = 0;
        int ySize = tileMap.size();
        int xSize = tileMap.get(0).size();
        int targetMapPos = -1;
        int objectID = -1;
        // the initial check does not count as a fail
        // first look at the near tiles using a kernel
        for(int i = -1; i < kernel-1; i++){
            int k_y = ref.getStartGPS()[0] + i; // y
            for(int j = -1; j < kernel-1; j++){
                int k_x = ref.getStartGPS()[1] + j; // x
                // make sure we are in bounds
                if(k_y >= 0 && k_x >= 0 && k_y <= ySize-1 && k_x <= xSize-1){
                    // loop through the objects in the tile and see if there is an object with the target type
                    for(InteractableObject obj : tileMap.get(k_y).get(k_x).getObjectsInTile()){
                        if(obj.getType().contains(getTaskTypes()[ref.getGoal()])){
                            // note some entities will have type restrictions for targets, child classes will define the logic
                            if(ref.getGoal() == 7 || ref.getGoal() == 1) {
                                String[] split = obj.getType().split("_");
                                //System.out.println(Integer.parseInt(split[split.length - 1]));
                                targetMapPos = Integer.parseInt(split[split.length - 2]);
                                objectID = Integer.parseInt(split[split.length - 1]);
                                ref.getLogger().writeNoTimestamp("Object type -> " + obj.getType());
                                ref.getLogger().writeNoTimestamp("Target position -> " + targetMapPos);
                                ref.getLogger().writeNoTimestamp("Object ID -> " + objectID);
                            }
                            return new int[]{k_y,k_x, targetMapPos, objectID};
                        }
                    }
                }
            }
        }//
        /* If we get here we need to look at other kernels */
        while(fails <= maxFail){
            int xPos = (int)Util.random(xSize);
            int yPos = (int)Util.random(ySize);
            for(int i = -1; i < kernel-1; i++){
                int k_y = xPos + i; // y
                for(int j = -1; j < kernel-1; j++){
                    int k_x = yPos + j; // x
                    // make sure we are in bounds
                    if(k_y >= 0 && k_x >= 0 && k_y <= ySize-1 && k_x <= xSize-1){
                        // loop through the objects in the tile and see if there is an object with the target type
                        for(InteractableObject obj : tileMap.get(k_y).get(k_x).getObjectsInTile()){
                            if(obj.getType().contains(getTaskTypes()[ref.getGoal()]) && obj.isActive()){
                                // note some entities will have type restrictions for targets, child classes will define the logic
                                if(ref.getGoal() == 7 || ref.getGoal() == 1) {
                                    String[] split = obj.getType().split("_");
                                    //System.out.println(Integer.parseInt(split[split.length - 1]));
                                    targetMapPos = Integer.parseInt(split[split.length - 2]);
                                    objectID = Integer.parseInt(split[split.length - 1]);
                                    ref.getLogger().writeNoTimestamp("Object type -> " + obj.getType());
                                    ref.getLogger().writeNoTimestamp("Target position -> " + targetMapPos);
                                    ref.getLogger().writeNoTimestamp("Object ID -> " + objectID);
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
        }
        // using the
        // default
        return new int[]{-1, -1, -1, -1};
    }// end of find tile

    // Path find using simple distance calculation
    public double calCost(int[] pos, int[] target){
        double vert = Math.pow(target[0] - pos[0], 2);
        double horz = Math.pow(target[1] - pos[1], 2);
        double sum = horz + vert;
        double result = Math.sqrt(sum);
        //System.out.println("sqrt(" + vert + " + " + horz + ") = " + result);
        return result;
    }
    // Find
    public List<List<Integer>> makePath(TaskRef ref,List<List<Tile>> tileMap){
        List<List<Integer>> moves = Collections.synchronizedList(new ArrayList<List<Integer>>());
        int [] current_pos = {ref.getStartGPS()[0], ref.getStartGPS()[1]};
        int [] targetTile = {ref.getTargetGPS()[0], ref.getTargetGPS()[1]};
        int turnsSpent = 0; // if this gets to 500 it will timeout
        int ySize = tileMap.size();
        int xSize = tileMap.get(0).size();
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
            ref.getLogger().write("Broke out of pathfind due to timeout");

        return moves;
    }

    public int makePriority(int goal){
        switch (goal){
            case 0:
            case 4:
                return 1;
            case 1:
            case 2:
                return 3;
            case 7:
                return 2;
            default: return 0;
        }
    }

    // reaching a tile does not always mean that the task is done
    public boolean targetTileReached(int []tile, TaskRef ref){
        return tile[0] == ref.getTargetGPS()[0] && tile[1] == ref.getTargetGPS()[1];
    }

    boolean isTaskFinished(TaskRef ref, int[] currTile, int seconds, int pos){
        if(ref.getTargetGPS()[0] != -1 && ref.getGoal() != 7) { // there are other things we need to check
            //System.out.println("[" + currTile[0] + " " + currTile[1] + "] -> [" + getTargetTile()[0] + " " + getTargetTile()[1] + "]");
            //System.out.println("[" + currTile[0] + " " + currTile[1] + "] -> [" + getTargetTile()[0] + " " + getTargetTile()[1] + "]");
            return currTile[0] == ref.getTargetGPS()[0] && currTile[1] == ref.getTargetGPS()[1];
        }//else{
            //System.out.println("[" + currTile[0] + " " + currTile[1] + "] -> [" + getTargetTile()[0] + " " + getTargetTile()[1] + "]");
        //}
        if(ref.getTargetTime() != 0)
            return (seconds >= ref.getTargetTime());

        if(ref.getGoal() == 7 && ref.getTargetGPS()[0] != -1){
            //System.out.println("goal of 7 checking");
            // find it another way
            //System.out.println("pos provided -> " + pos);
            if(pos > -1){
                int tmpId = World.subMaps.get(pos).getObjectID();
                //System.out.println("tmpID -> " + tmpId);
                //System.out.println(World.tileMap.get(targetTile[0]).get(targetTile[1]).getObjectsInTile().get(tmpId).isActive());
                if(World.tileMap.get(ref.getTargetGPS()[0]).get(ref.getTargetGPS()[1]).getObjectsInTile().size() > 0)
                    return (!World.tileMap.get(ref.getTargetGPS()[0]).get(ref.getTargetGPS()[1]).getObjectsInTile().get(tmpId).isActive()); // we want the oppisite of this guy
                else
                    return true; // if we get to this that means there is an error with with the objects, so lets forget about it
            }
        }
        return false;
    }
}