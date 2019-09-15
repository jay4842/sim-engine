package luna.entity;

import luna.util.Tile;
import luna.util.Util;
import luna.world.objects.InteractableObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// How entities will want to do things
// This will define short term objectives
public class Task {
    String [] taskTypes = {"none","food", "rest", "move", "wander", "attack", "train", "group", "breed"};
    int goal;
    boolean taskSet = false;
    int [] startPos = new int[2];
    int [] targetTile = {-1,-1};
    int targetTime = 0;
    int timeSpent = 0;
    List<int[]> moves = new ArrayList<>();
    //
    public Task(int [] startPos, int goal){
        this.startPos[0] = startPos[0];
        this.startPos[1] = startPos[1];
        this.goal = goal;
    }//

    // will add more logic as needed
    public void makeTask(List<List<Tile>> tileMap, int seconds){
        if(goal == 1 || goal == 3) // more later
            targetTile = findTile(tileMap);
        if(goal == 2){
            this.targetTime = seconds + 10; // wait ten seconds
        }
        taskSet = true;
    }

    // bad practice but putting parameter specific values here, I don't want these to be passed by the function call
    protected int kernel = 3; // protected, child classes may change ho this works
    protected int maxFail = 10;
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
        int xPos = Util.random(xSize);
        int yPos = Util.random(ySize);
        while(fails <= maxFail){
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
            }
            // we didn't find one
            fails ++;
        }
        // using the
        // default
        return new int[]{-1,-1};
    }

    // Path find using simple distance calculation
    public int calCost(int[] pos, int[] target){
        return (int)Math.sqrt(((target[0] - pos[0])^2) + ((target[1] - pos[1])^2));
    }
    //
    public List<List<Integer>> makePath(int[] targetTile,List<List<Tile>> tileMap){
        List<List<Integer>> moves = Collections.synchronizedList(new ArrayList<List<Integer>>());
        // TODO add finding path logic

        return moves;
    }

    // oaky so on the entity side they will handle the final transaction, like the actual task action that happens once
    //  you get the target tile or time has just run out for the task.
    public boolean isTaskFinished(int[] currTile, int seconds){
        if(getTargetTile()[0] != -1)
            return (currTile[0] == getTargetTile()[0] && currTile[1] == getTargetTile()[1]);
        if(targetTime != 0)
            return (seconds >= targetTime);

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

    public List<int[]> getMoves() {
        return moves;
    }

    public void setMoves(List<int[]> moves) {
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
}
