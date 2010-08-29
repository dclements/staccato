package com.readytalk.staccato.database.migration.workflow;

import java.io.File;
import java.io.IOException;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationResult;
import com.readytalk.staccato.database.migration.MigrationRuntime;
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
    EasyMock.replay(script);

    MigrationAnnotationParser annotationParser = new MigrationAnnotationParserImpl();

    MigrationWorkflowServiceImpl workflowService = new MigrationWorkflowServiceImpl(annotationParser);

    MigrationRuntime runtime = EasyMock.createMock(MigrationRuntime.class);

    MigrationResult result = workflowService.executeWorkflow(script, new Class[]{TestWorkflowStepOne.class, TestWorkflowStepTwo.class}, runtime);

    Assert.assertNotNull(result);
    Assert.assertEquals(result.getResultMap().get(TestWorkflowStepOne.class), "bar");
  }
}
