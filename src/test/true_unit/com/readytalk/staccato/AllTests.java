package com.readytalk.staccato;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.readytalk.staccato.database.DatabaseServiceTest;
import com.readytalk.staccato.database.JUDatabaseTypeTest;
import com.readytalk.staccato.database.migration.GroovyMigrationServiceTest;
import com.readytalk.staccato.database.migration.MigrationLoggingServiceTest;
import com.readytalk.staccato.database.migration.MigrationRuntimeTest;
import com.readytalk.staccato.database.migration.script.groovy.JUGroovyScriptServiceTest;
import com.readytalk.staccato.database.migration.script.groovy.JUGroovyScriptTest;
import com.readytalk.staccato.database.migration.script.sql.JUSQLScriptServiceTest;
import com.readytalk.staccato.database.migration.script.sql.JUSQLScriptTest;
import com.readytalk.staccato.database.migration.validation.javax.JUDateFormatValidatorTest;
import com.readytalk.staccato.database.migration.validation.javax.MigrationTypeValidatorTest;
import com.readytalk.staccato.database.migration.validation.javax.MigrationValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({CommandLineServiceTest.class, DatabaseServiceTest.class, JUDatabaseTypeTest.class, GroovyMigrationServiceTest.class,
	MigrationLoggingServiceTest.class, MigrationRuntimeTest.class, JUGroovyScriptServiceTest.class, JUGroovyScriptTest.class,
	JUSQLScriptServiceTest.class, JUSQLScriptTest.class, JUDateFormatValidatorTest.class, MigrationTypeValidatorTest.class,
	MigrationValidatorTest.class})
public class AllTests {

}
