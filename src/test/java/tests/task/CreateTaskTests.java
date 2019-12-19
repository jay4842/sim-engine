package tests.task;

import luna.entity.Entity;
import luna.entity.util.EntityManager;
import luna.entity.util.TaskRef;
import luna.util.Tile;
import luna.util.Util;
import luna.world.util.ObjectManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import tests.world.WorldTest;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CreateTaskTests {
    ObjectManager objectManager;
    Util util;
    List<List<Tile>> tileMap;
    List<Entity> entities;
    int spawnLimit = 1;
    int width;
    int height;
    int world_scale;

    @Before
    public void setupData(){
        objectManager = new ObjectManager();
        util = new Util();
        tileMap = WorldTest.makeTestMap();
        world_scale = tileMap.get(0).get(0).getWorld_scale();
        height = tileMap.size() * world_scale;
        width = tileMap.get(0).size() * world_scale;
        int entityCount = 0;

        entities = new ArrayList<>();
        for (int i = 0; i < spawnLimit; i++) {
            int x = Util.random(width);
            int y = Util.random(height);
            //x = 32*5;
            //y = 32*5;
            Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            entities.add(new Entity(x, y, this.width, this.height, world_scale, c, entityCount, true));
            System.out.println(entities.get(i).toString());
            entityCount += 1;
        }


    }

    @Test
    public void GatherResourceTest(){
        int seconds = 0;
        String [] resources = new String[]{"gather_wood", "gather_stone"};

        System.out.println(entities.get(0).toString());
        for(String need : resources){
            TaskRef task = new TaskRef(entities.get(0).getEntityID(), need,
                new int[]{entities.get(0).getCurrTileY(), entities.get(0).getCurrTileY(),
                        entities.get(0).getPosition()}, tileMap, 0);
            System.out.println(task.toString());
            Assertions.assertTrue(task.getTaskUtil().isValid(task));
        }
    }
}
