package com.readytalk.staccato.database.migration.workflow;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.MigrationLoggingService;
import com.readytalk.staccato.database.migration.MigrationResult;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParserImpl;
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepOne;
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepTwo;
import com.readytalk.staccato.database.migration.guice.MigrationModule;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

public class MigrationWorkflowServiceImplTest {
	
	private final static Injector injector = Guice.createInjector(new MigrationModule());

	// Tests running a migration using a test workflow and step

	@Test
	@SuppressWarnings("unchecked")
	public void testExecute() throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException {

		File file = new File("src/test/unit/groovy/TestScript.groovy");

		GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
		Class<?> scriptClass = gcl.parseClass(new GroovyCodeSource(file.toURI().toURL()));
		Object scriptInstance = scriptClass.newInstance();

		DynamicLanguageScript<?> script = mock(DynamicLanguageScript.class);
		when(script.getScriptInstance()).thenReturn(scriptInstance);

		DatabaseContext dbCtx = mock(DatabaseContext.class);
		MigrationRuntime runtime = mock(MigrationRuntime.class);
		when(runtime.getDatabaseContext()).thenReturn(dbCtx);
		when(runtime.isLoggingEnabled()).thenReturn(true);

		MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

		Migration migrationAnnotation = mock(Migration.class);
		when(migrationAnnotation.databaseVersion()).thenReturn("1.0");

		MigrationLoggingService migrationLoggingService = mock(MigrationLoggingService.class);
		migrationLoggingService.log(dbCtx, script, TestWorkflowStepOne.class, migrationAnnotation);
		migrationLoggingService.log(dbCtx, script, TestWorkflowStepTwo.class, migrationAnnotation);

		MigrationWorkflowServiceImpl<?> workflowService = new MigrationWorkflowServiceImpl(annotationParser, migrationLoggingService, injector);

		MigrationResult result = workflowService.executeWorkflow(script, new Class[]{TestWorkflowStepOne.class, TestWorkflowStepTwo.class}, runtime);

		Assert.assertNotNull(result);
		Assert.assertEquals(result.getResultMap().get(TestWorkflowStepOne.class), "bar");
	}
}
