package luna.entity;

import luna.util.Tile;

import java.util.ArrayList;
import java.util.List;

// How entities will want to do things
// This will define short term objectives
public class Task {
    String [] taskTypes = {"none","food", "rest", "move", "wander", "attack", "train", "group", "breed"};
    int goal;
    boolean taskSet = false;
    int [] startPos = new int[2];
    int [] targetTile = {0,0};
    int targetTime = 0;
    int timeSpent = 0;
    List<int[]> moves = new ArrayList<>();
    //
    public Task(int [] startPos, int goal){
        this.startPos[0] = startPos[0];
        this.startPos[1] = startPos[1];
        this.goal = goal;
    }//

    //TODO: define make task logic
    public void makeTask(List<List<Tile>> tileMap, int seconds){
        targetTile = findTile(tileMap);
        taskSet = true;
    }

    // bad practice but putting parameter specific values here
    int kernel = 3;
    int maxFail = 10;
    int tileType = 0;
    String objectType = "None";
    // TODO: define find Tile Logic
    public int [] findTile(List<List<Tile>> tileMap){

        // default
        return new int[]{-1,-1};
    }

    // oaky so on the entity side they will handle the final transaction, like the actual task action that happens once
    //  you get the target tile or time has just run out for the task.
    public boolean isTaskFinished(int[] currTile, int seconds){
        if(getTargetTile()[0] != -1)
            return (currTile[0] == getTargetTile()[0] && currTile[1] == getTargetTile()[1]);
        if(targetTime != 0)
            return (seconds == targetTime);

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
