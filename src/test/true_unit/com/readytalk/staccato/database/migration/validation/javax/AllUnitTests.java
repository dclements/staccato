package com.readytalk.staccato.database.migration.validation.javax;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({DateFormatValidatorTest.class, VersionValidatorTest.class, MigrationTypeValidatorTest.class, MigrationValidatorTest.class})
public class AllUnitTests {
	
}
