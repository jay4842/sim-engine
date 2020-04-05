package org.luna.test.core.util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.luna.core.util.SimUtility;
import org.luna.core.util.ZipUtil;

import java.util.ArrayList;
import java.util.List;

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
    public void testGlob(){
        String path = "logs/entity/sim_0/*.txt";
        List<String> files = SimUtility.glob(path);
        assertTrue(files.size() > 0);
    }

    @Test
    public void testZipFile(){
        ZipUtil zip = new ZipUtil();
        String root = System.getProperty("user.home") + "/IdeaProjects/sim-engine/";
        String folderName = System.getProperty("user.home") + "/IdeaProjects/sim-engine/logs";
        assertTrue(zip.createZipFile(folderName, root + "tmp/tmp.zip"));
    }

    @Test
    public void testSendZipFile(){
        String target = System.getProperty("user.home") + "/IdeaProjects/sim-engine/logs";
        assertTrue(SimUtility.sendFolderOverSftp(target, "logs.zip"));
    }
}
