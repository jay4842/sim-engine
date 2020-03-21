package org.luna.test.logic.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.luna.logic.service.PersonalityManager;

public class PersonalityManagerTest {

    private PersonalityManager manager;

    @Before
    public void setup(){
        manager = new PersonalityManager();
    }

    @Test
    public void loadPersonalityTest(){
        int result = manager.loadPersonalityFile();
        Assertions.assertTrue(result > 0);
    }
}
