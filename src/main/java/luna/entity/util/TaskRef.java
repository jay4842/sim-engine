package luna.entity.util;

import luna.util.Logger;
import luna.util.Tile;
import luna.world.World;
import luna.world.objects.InteractableObject;

import java.util.ArrayList;
import java.util.List;

public class TaskRef implements Comparable<TaskRef>{

    private static int counter = 0;
    private static TaskUtil taskUtil = new TaskUtil();
    private int entityID;
    private int goal;
    private int[] startGPS;
    private int[] targetGPS;
    private int priority;
    private boolean inProgress;
    private int targetTime;
    private String notes;
    private int xp;
    private int refId;
    protected List<List<Integer>> moves = new ArrayList<>();

    //
    public TaskRef(){
      refId = counter;
      counter++;
      this.entityID = -1;
      this.goal = -1;
      this.startGPS = new int[]{-1,-1,-1,-1};
      this.inProgress = false;
      this.targetTime = -1;
      this.notes = "should not be used";
    }

    public TaskRef(int entityID, int goal, int[]startGPS, List<List<Tile>> tileMap, int seconds){
        refId = counter;
        counter++;
        this.entityID = entityID;
        this.goal = goal;
        this.startGPS = startGPS;
        this.inProgress = false;
        this.targetTime = -1;
        this.notes = "";
        // TODO: finish adding setting up tasks here
        makeTask(taskUtil.makeTask(this, tileMap, seconds));
        this.priority = taskUtil.makePriority(this.goal);
    }

    public TaskRef(int entityID, String goal, int[]startGPS, List<List<Tile>> tileMap, int seconds){
        counter++;
        this.entityID = entityID;
        this.goal = taskUtil.getTaskType(goal);
        this.startGPS = startGPS;
        this.inProgress = false;
        this.targetTime = -1;
        this.notes = "";
        // TODO: finish adding setting up tasks here
        makeTask(taskUtil.makeTask(this, tileMap, seconds));
        this.priority = taskUtil.makePriority(this.goal);
    }

    // Getter/setters
    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int[] getStartGPS() {
        return startGPS;
    }

    public void setStartGPS(int[] startGPS) {
        this.startGPS = startGPS;
    }

    public int[] getTargetGPS() {
        return targetGPS;
    }

    public void setTargetGPS(int[] targetGPS) {
        this.targetGPS = targetGPS;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean inProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public static int getCounter() {
        return counter;
    }

    public int getEntityID() {
        return entityID;
    }

    public List<List<Integer>> getMoves() {
        return moves;
    }

    public void setMoves(List<List<Integer>> moves) {
        this.moves = moves;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getRefId() {
        return refId;
    }

    private void makeTask(TaskRef ref){
        setMoves(ref.getMoves());
        setTargetTime(ref.getTargetTime());
        setTargetGPS(ref.getTargetGPS());

        //
        if(ref.getTaskType().equals("hostile")){
            setXp(10); // TODO: change depending on difficulty
        }
    }

    @Override
    public int compareTo(TaskRef ref) {
        return Integer.compare(ref.getPriority(), this.priority);
    }

    public boolean isFinished(int[] currTile, int seconds, int pos){
        return taskUtil.isTaskFinished(this, currTile, seconds, pos);
    }

    public boolean targetTileReached(int []tile){
        return taskUtil.targetTileReached(tile, this);
    }

    public String getTaskType(){return taskUtil.getTaskTypes()[getGoal()];}

    public TaskUtil getTaskUtil(){return taskUtil;}

    public InteractableObject getObject(){
        if(getTargetGPS()[3] != -1){
            if(getTargetGPS()[2] != -1)
                return World.getMap(getTargetGPS()[2]).getTileMap().get(getTargetGPS()[0]).get(getTargetGPS()[1]).getObjectsInTile().get(getTargetGPS()[3]);
            else
                return World.tileMap.get(getTargetGPS()[0]).get(getTargetGPS()[1]).getObjectsInTile().get(getTargetGPS()[3]);

        }
        return null;
    }//
}
