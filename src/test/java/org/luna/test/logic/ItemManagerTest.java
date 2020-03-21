package org.luna.test.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.luna.logic.service.ItemManager;

public class ItemManagerTest {
    private ItemManager manager;

    @Before
    public void testSetup(){
        // any opens/setups needed
        manager = new ItemManager(1);
    }

    @After
    public void tearDown(){
        // any saves/closes
    }

    @Test
    public void testItemLoad(){
        Assertions.assertTrue(manager.createItemRefs() > 0);
    }
}
