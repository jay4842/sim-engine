package main;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class TestRunner extends Runner {

    private Class testClass;
    public TestRunner(Class testClass){
        super();
        this.testClass = testClass;
    }//

    @Override
    public Description getDescription() {
        return Description
                .createTestDescription(testClass, "Basic test runner example");
    }

    @Override
    public void run(RunNotifier notifier) {

    }
}
