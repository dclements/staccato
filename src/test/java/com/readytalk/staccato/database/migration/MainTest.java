package com.readytalk.staccato.database.migration;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class MainTest {

    @Test
    public void testHelpWithArg() {
        try {
            Main.main("help");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testHelpWithOutArg() {
        try {
            Main.main();
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
