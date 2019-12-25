package tests;

import luna.util.Manager;
import luna.world.World;
import luna.world.objects.item.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ItemTests {
    Manager manager;


    @Before
    public void setup() {
        manager = new Manager();
        manager.createItemRefs();
    }

    @Test
    public void createItemTests(){

        // create several items and assert that they are not null
        String type = "resource_stone_-1_1";
        String [] split = type.split("_");
        String itemType = split[0] + "_" + split[1];
        Item wood = (Item) manager.call("post_makeTestItem", itemType);
        Assertions.assertNotNull(wood);
    }
}
