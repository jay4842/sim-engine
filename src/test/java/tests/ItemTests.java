package tests;

import luna.world.objects.item.Item;
import luna.world.objects.item.ItemMaker;
import luna.world.util.ObjectManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ItemTests {
    ObjectManager objectManager;


    @Before
    public void setup(){
        objectManager = new ObjectManager();
    }

    @Test
    public void createItemTests(){

        // create several items and assert that they are not null
        String type = "resource_stone_-1_1";
        String [] split = type.split("_");
        String itemType = split[0] + "_" + split[1];
        Item wood = objectManager.createItem(itemType);
        Assertions.assertNotNull(wood);
    }
}
