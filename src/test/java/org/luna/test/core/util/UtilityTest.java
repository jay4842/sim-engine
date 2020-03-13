package org.luna.test.core.util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.luna.core.util.Utility;

public class UtilityTest {

    @Before
    public void testSetup(){
        // any opens/setups needed
    }

    @After
    public void tearDown(){
        // any saves/closes
    }

    @Test
    public void testSftp(){
        String file = "res/entity/personality/personalities.json";
        Assertions.assertTrue(Utility.sendFileOverSftp(file));
    }

    @Test
    public void testSendZipFile(){
        String target = "C:\\Users\\Paddington\\IdeaProjects\\sim-engine\\logs";
        Assertions.assertTrue(Utility.sendFolderOverSftp(target));
    }

}
