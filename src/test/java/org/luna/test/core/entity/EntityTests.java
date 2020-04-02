package org.luna.test.core.entity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.luna.core.entity.Entity;

public class EntityTests {
    private Entity testEntityA;
    private Entity testEntityB;

    @Before
    public void testSetup(){
        // any opens/setups needed
        testEntityA = new Entity(32, new int[]{10,10,0}, -1);
        testEntityB = new Entity(32, new int[]{15,15,0}, -1);
    }

    @After
    public void tearDown(){
        // any saves/closes
        testEntityA.shutdown();
        testEntityB.shutdown();
    }

    @Test
    public void testEntityInteraction(){
        String output = testEntityA.interact(testEntityB);
        System.out.println(output);
        Assertions.assertTrue(output.length() > 0);
        String[] cmdSplit = output.split(",");
        float val = Float.parseFloat(cmdSplit[cmdSplit.length-1]);
        System.out.println(val);
        float result = testEntityB.receiveInteraction(testEntityA.getId(), val);
        System.out.println(result);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testEntityReportLines(){
        String entityManagerLine = testEntityA.makeReportLine();
        System.out.println(entityManagerLine);
        String entityReportLine = testEntityA.makeEntityReportLine(1).toJSONString();
        System.out.println(entityReportLine);
        Assertions.assertTrue(entityManagerLine.length() > 0);
        Assertions.assertTrue(entityReportLine.length() > 0);
    }

}
