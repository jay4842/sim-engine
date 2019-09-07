package luna.entity;

import luna.util.Tile;

import java.util.ArrayList;
import java.util.List;

// How entities will want to do things
// This will define short term objectives
public class Task {
    String [] taskTypes = {"food", "rest", "move", "wander", "attack", "train", "group", "breed"};
    int goal;
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
}
