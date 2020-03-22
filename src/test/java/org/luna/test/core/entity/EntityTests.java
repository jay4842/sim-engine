package org.luna.test.core.entity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.luna.core.entity.Entity;

public class EntityTests {
    private Entity testEntity;

    @Before
    public void testSetup(){
        // any opens/setups needed
        testEntity = new Entity(32, new int[]{10,10,0}, 1);
    }

    @After
    public void tearDown(){
        // any saves/closes
        testEntity.shutdown();
    }

    @Test
    public void testEntityInteraction(){

    }

    @Test
    public void testEntityReportLines(){
        String entityManagerLine = testEntity.makeReportLine();
        System.out.println(entityManagerLine);
        String entityReportLine = testEntity.makeEntityReportLine(1).toJSONString();
        System.out.println(entityReportLine);
        Assertions.assertTrue(entityManagerLine.length() > 0);
        Assertions.assertTrue(entityReportLine.length() > 0);
    }

}
