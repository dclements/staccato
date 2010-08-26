package com.readytalk.staccato.database.migration.workflow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepOne;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

/**
 * @author jhumphrey
 */
public class WorkflowStepExecutorImplTest {

  @Test
  public void testExecute() throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

    File file = new File("src/test/groovy/TestScript.groovy");

    // test with a groovy script
    DynamicLanguageScript script = EasyMock.createStrictMock(DynamicLanguageScript.class);
    EasyMock.expect(script.getUrl()).andReturn(file.toURI().toURL());
    EasyMock.expect(script.getFilename()).andReturn(file.getName());
    EasyMock.expect(script.getUrl()).andReturn(file.toURI().toURL());
    EasyMock.replay(script);

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new GroovyCodeSource(script.getUrl()));

    Object scriptInstance = scriptClass.newInstance();
    Method testMethod = scriptInstance.getClass().getMethod("testMethodOne", MigrationRuntime.class);

    TestWorkflowStepOne testWorkflowStepOne = testMethod.getAnnotation(TestWorkflowStepOne.class);

    Assert.assertEquals(testWorkflowStepOne.value(), "foo");

    MigrationAnnotationParser annotationParser = EasyMock.createStrictMock(MigrationAnnotationParser.class);
    EasyMock.expect(annotationParser.getAnnotatedMethod(scriptInstance, testWorkflowStepOne.annotationType())).andReturn(testMethod);
    EasyMock.replay(annotationParser);

    WorkflowStepExecutorImpl<TestWorkflowStepOne> executor = new WorkflowStepExecutorImpl<TestWorkflowStepOne>();
    executor.initialize(testWorkflowStepOne);

    MigrationRuntime runtime = EasyMock.createMock(MigrationRuntime.class);

    try {
      Object result = executor.execute(scriptInstance, new WorkflowContext(annotationParser, runtime));

      Assert.assertEquals(result, "bar");
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

  }
}
