package luna.util.task;

// This guy will hold evey thing that is needed to build a task
// - Task types
// - Any resources like images/animations
// - helper functions to optimize Task.java
public class TaskUtil {

    String [] taskTypes;
    String [] buildingTypes;

    public TaskUtil(){
        taskTypes = new String[]{"none", "food", "rest", "move", "wander",
                                 "attack", "train", "hostile","base", "group",
                                 "breed"};
        buildingTypes = new String[]{"camp"};
    }//

    public String[] getTaskTypes(){return taskTypes;}
}
