package com.readytalk.staccato.database.migration;

import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.junit.runner.RunWith;

@RunWith(Suite.class)
@SuiteClasses({GroovyMigrationServiceTest.class, MigrationLoggingServiceTest.class, MigrationRuntimeTest.class})
public class AllUnitTests {

}
