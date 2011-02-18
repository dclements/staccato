package com.readytalk.staccato.database.migration.workflow;

import java.io.File;
import java.io.IOException;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.MigrationLoggingService;
import com.readytalk.staccato.database.migration.MigrationResult;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParserImpl;
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepOne;
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepTwo;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

/**
 * @author jhumphrey
 */
public class MigrationWorkflowServiceImplTest {

  // Tests running a migration using a test workflow and step

  @Test
  @SuppressWarnings("unchecked")
  public void testExecute() throws IOException, NoSuchMethodException, IllegalAccessException, InstantiationException {

    File file = new File("src/test/unit/groovy/TestScript.groovy");

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new GroovyCodeSource(file.toURI().toURL()));
    Object scriptInstance = scriptClass.newInstance();

    DynamicLanguageScript script = EasyMock.createStrictMock(DynamicLanguageScript.class);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.expect(script.getScriptInstance()).andReturn(scriptInstance);
    EasyMock.replay(script);

    DatabaseContext dbCtx = EasyMock.createStrictMock(DatabaseContext.class);
    MigrationRuntime runtime = EasyMock.createMock(MigrationRuntime.class);
    EasyMock.expect(runtime.getDatabaseContext()).andReturn(dbCtx).times(2);
    EasyMock.expect(runtime.isLoggingEnabled()).andReturn(true).times(2);
    EasyMock.replay(runtime);

    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

    Migration migrationAnnotation = EasyMock.createStrictMock(Migration.class);
    EasyMock.expect(migrationAnnotation.databaseVersion()).andReturn("1.0");
    EasyMock.expect(migrationAnnotation.databaseVersion()).andReturn("1.0");
    EasyMock.replay(migrationAnnotation);

    MigrationLoggingService migrationLoggingService = EasyMock.createNiceMock(MigrationLoggingService.class);
    migrationLoggingService.log(dbCtx, script, TestWorkflowStepOne.class, migrationAnnotation);
    migrationLoggingService.log(dbCtx, script, TestWorkflowStepTwo.class, migrationAnnotation);
    EasyMock.replay(migrationLoggingService);

    MigrationWorkflowServiceImpl workflowService = new MigrationWorkflowServiceImpl(annotationParser, migrationLoggingService);

    MigrationResult result = workflowService.executeWorkflow(script, new Class[]{TestWorkflowStepOne.class, TestWorkflowStepTwo.class}, runtime);

    Assert.assertNotNull(result);
    Assert.assertEquals(result.getResultMap().get(TestWorkflowStepOne.class), "bar");
  }
}
