package org.luna.test.core.util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.luna.core.util.SimUtility;
import org.luna.core.util.ZipUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimUtilityTest {

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
        assertTrue(SimUtility.sendFileOverSftp(file));
    }

    @Test
    public void testZipFile(){
        ZipUtil zip = new ZipUtil();
        String root = "/Users/jelly_kid/IdeaProjects/sim-engine/";
        String folderName = "/Users/jelly_kid/IdeaProjects/sim-engine/logs";
        assertTrue(zip.createZipFile(folderName, root + "tmp/tmp.zip"));
    }

    @Test
    public void testSendZipFile(){
        String target = "/Users/jelly_kid/IdeaProjects/sim-engine/logs";
        assertTrue(SimUtility.sendFolderOverSftp(target));
    }
}
