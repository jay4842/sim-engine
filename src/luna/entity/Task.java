package luna.entity;

import luna.util.Logger;
import luna.util.Tile;
import luna.util.Util;
import luna.world.World;
import luna.world.objects.InteractableObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// How entities will want to do things
// This will define short term objectives
public class Task {
    String [] taskTypes = {"none","food", "rest", "move", "wander", "attack", "train", "hostile", "group", "breed"};
    protected int goal;
    protected boolean taskSet = false;
    protected int [] startPos = new int[2];
    protected int [] targetTile = {-1,-1};
    protected int targetTime = 0;
    protected int timeSpent = 0;
    protected int targetMapPos = -1;
    protected int objectID = -1; // if we are going for an encounter then there should be an object to ref it.
    protected List<List<Integer>> moves = new ArrayList<>();
    protected Logger logger;

    //
    public Task(int [] startPos, int goal, int id){
        this.startPos[0] = startPos[0];
        this.startPos[1] = startPos[1];
        this.goal = goal;
        this.logger = new Logger("./logs/TaskLogs/Entity_" + id + "_task.txt");
    }//

    public Task(int [] startPos, int goal, int id, String filename){
        this.startPos[0] = startPos[0];
        this.startPos[1] = startPos[1];
        this.goal = goal;
        this.logger = new Logger(filename + id + "_task.txt");
    }//

    // will add more logic as needed
    public void makeTask(List<List<Tile>> tileMap, int seconds){
        logger.write("----------- Making Task, Goal = " + taskTypes[goal] + " -----------");
        if(goal == 1 || goal == 3 || goal == 7) { // more later
            targetTile = findTile(tileMap);
            logger.writeNoTimestamp("Moving To Target");
            logger.writeNoTimestamp("[" + startPos[0] +" " + startPos[1] + "] -> [" + targetTile[0] + " " + targetTile[1] +  "]");
            logger.writeNoTimestamp("Moves Found");
            moves = makePath(targetTile,tileMap);
            for(List<Integer> move : moves){
                logger.writeNoTimestamp("[" + move.get(0) + " " + move.get(1) + "]");
            }
            // show the map for the logger
            String buffer = "";
            for(int i = 0; i < tileMap.size(); i++){
                for(int j = 0; j < tileMap.get(i).size(); j++){
                    if(startPos[0] == i && startPos[1] == j)
                        buffer += "A ";
                    else if(targetTile[0] == i && targetTile[1] == j)
                        buffer += "B ";
                    else buffer += "_ ";
                }
                logger.writeNoTimestamp(buffer);
                buffer = "";
            }
        }
        if(goal == 2){
            this.targetTime = seconds + 10; // wait ten seconds
        }
        logger.writeNoTimestamp("Target TileMapPos = " + this.targetMapPos);
        logger.writeNoTimestamp("-------------------------------------------------------------------------\n");
        taskSet = true;
        System.out.println("tileMapPos = " + this.targetMapPos);

    }

    // bad practice but putting parameter specific values here, I don't want these to be passed by the function call
    protected int kernel = 3; // protected, child classes may change ho this works
    protected int maxFail = 100;
    protected int tileType = 0;
    String objectType = "None";
    // Find a tile by looking at the adjacent tiles, then randomly looking around the map
    // - This should not be too difficult, also it may depend on the entities search range later
    public int [] findTile(List<List<Tile>> tileMap){
        int fails = 0;
        int ySize = tileMap.size();
        int xSize = tileMap.get(0).size();

        // the initial check does not count as a fail
        // first look at the near tiles using a kernel
        for(int i = -1; i < kernel-1; i++){
            int k_y = this.startPos[0] + i; // y
            for(int j = -1; j < kernel-1; j++){
                int k_x = this.startPos[1] + j; // x
                // make sure we are in bounds
                if(k_y >= 0 && k_x >= 0 && k_y <= ySize-1 && k_x <= xSize-1){
                    // loop through the objects in the tile and see if there is an object with the target type
                    for(InteractableObject obj : tileMap.get(k_y).get(k_x).getObjectsInTile()){
                        if(obj.getType().contains(taskTypes[goal])){
                            // note some entities will have type restrictions for targets, child classes will define the logic
                            return new int[]{k_y,k_x};
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
                            if(obj.getType().contains(taskTypes[goal]) && obj.isActive()){
                                // note some entities will have type restrictions for targets, child classes will define the logic
                                if(goal == 7) {
                                    String[] split = obj.getType().split("_");
                                    System.out.println(Integer.parseInt(split[split.length - 1]));
                                    this.targetMapPos = Integer.parseInt(split[split.length - 1]);
                                }else if(goal == 1){
                                    //check if its a sub map food
                                    String[] split = obj.getType().split("_");
                                    //System.out.println(obj.getType());
                                    if(split.length > 2){
                                        //System.out.println(Integer.parseInt(split[split.length - 1]));
                                        logger.writeNoTimestamp("Object type -> " + obj.getType());
                                        this.targetMapPos = Integer.parseInt(split[split.length - 1]);
                                        this.objectID = obj.getObjectID();
                                    }else
                                        this.targetMapPos = -1;
                                }
                                //
                                return new int[]{k_y,k_x};
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
        return new int[]{-1,-1};
    }

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
    public List<List<Integer>> makePath(int[] targetTile,List<List<Tile>> tileMap){
        List<List<Integer>> moves = Collections.synchronizedList(new ArrayList<List<Integer>>());
        int [] current_pos = {startPos[0], startPos[1]};
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
                logger.write("[" + move.get(0) + " " + move.get(1) + "] -> [" + targetTile[0] + " " + targetTile[1] + "] = " + dist);
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
            logger.write("Broke out of pathfind due to timeout");

        return moves;
    }

    // oaky so on the entity side they will handle the final transaction, like the actual task action that happens once
    //  you get the target tile or time has just run out for the task.
    public boolean isTaskFinished(int[] currTile, int seconds){
        if(getTargetTile()[0] != -1) {
            if(currTile[0] == getTargetTile()[0] && currTile[1] == getTargetTile()[1]) {
                //System.out.println("[" + currTile[0] + " " + currTile[1] + "] -> [" + getTargetTile()[0] + " " + getTargetTile()[1] + "]");
                return true;
            }else{
                //System.out.println("[" + currTile[0] + " " + currTile[1] + "] -> [" + getTargetTile()[0] + " " + getTargetTile()[1] + "]");
                return false;
            }
        }else{
            //System.out.println("[" + currTile[0] + " " + currTile[1] + "] -> [" + getTargetTile()[0] + " " + getTargetTile()[1] + "]");
        }
        if(targetTime != 0)
            return (seconds >= targetTime);

        if(goal == 6){
            if(!World.tileMap.get(targetTile[0]).get(targetTile[1]).getObjectsInTile().get(objectID).isActive()){
                // lets double check here
                World.subMaps.get(targetMapPos).makeEntityRefs();

            }
        }
        return false;
    }
    // getters and setter
    public String[] getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(String[] taskTypes) {
        this.taskTypes = taskTypes;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
        this.setTaskSet(false);
        //System.out.println("goal changes to " + goal);
    }

    public int[] getStartPos() {
        return startPos;
    }

    public void setStartPos(int[] startPos) {
        this.startPos = startPos;
    }

    public int[] getTargetTile() {
        return targetTile;
    }

    public void setTargetTile(int[] targetTile) {
        this.targetTile = targetTile;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public int getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }

    public List<List<Integer>> getMoves() {
        return moves;
    }

    public void setMoves(List<List<Integer>> moves) {
        this.moves = moves;
    }

    public int getKernel() {
        return kernel;
    }

    public void setKernel(int kernel) {
        this.kernel = kernel;
    }

    public int getMaxFail() {
        return maxFail;
    }

    public void setMaxFail(int maxFail) {
        this.maxFail = maxFail;
    }

    public int getTileType() {
        return tileType;
    }

    public void setTileType(int tileType) {
        this.tileType = tileType;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public boolean isTaskSet() {
        return taskSet;
    }

    public void setTaskSet(boolean taskSet) {
        this.taskSet = taskSet;
    }

    public int getTargetMapPos() {
        return targetMapPos;
    }

    public void setTargetMapPos(int targetMapPos) {
        this.targetMapPos = targetMapPos;
    }

    public int getObjectID() {
        return objectID;
    }


    // copy another task
    public Task clone(Task t){
        logger.write("cloning task with goal = " + t.getGoal());
        this.goal = t.goal;
        this.moves = t.moves;
        this.targetTile = t.targetTile;
        this.targetTime = t.targetTime;
        this.targetMapPos = t.targetMapPos;
        this.startPos = t.startPos;
        this.timeSpent = t.timeSpent;
        return this;
    }
}
