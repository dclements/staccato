package com.readytalk.staccato.database.migration.workflow;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.annotation.TestWorkflowStepOne;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

public class WorkflowStepExecutorImplTest {

	@Test
	public void testExecute() throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

		File file = new File("src/test/unit/groovy/TestScript.groovy");

		// test with a groovy script
		DynamicLanguageScript<?> script = mock(DynamicLanguageScript.class);
		when(script.getUrl()).thenReturn(file.toURI().toURL());
		when(script.getFilename()).thenReturn(file.getName());
		when(script.getUrl()).thenReturn(file.toURI().toURL());

		GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
		Class<?> scriptClass = gcl.parseClass(new GroovyCodeSource(script.getUrl()));

		Object scriptInstance = scriptClass.newInstance();
		Method testMethod = scriptInstance.getClass().getMethod("testMethodOne", MigrationRuntime.class);

		TestWorkflowStepOne testWorkflowStepOne = testMethod.getAnnotation(TestWorkflowStepOne.class);

		Assert.assertEquals(testWorkflowStepOne.value(), "foo");

		MigrationAnnotationParser annotationParser = mock(MigrationAnnotationParser.class);
		when(annotationParser.getAnnotatedMethod(eq(scriptInstance), eq(testWorkflowStepOne.annotationType()))).thenReturn(testMethod);

		WorkflowStepExecutorImpl<TestWorkflowStepOne> executor = new WorkflowStepExecutorImpl<TestWorkflowStepOne>();
		executor.initialize(testWorkflowStepOne);

		MigrationRuntime runtime = mock(MigrationRuntime.class);

		try {
			Object result = executor.execute(scriptInstance, new WorkflowContextImpl(annotationParser, runtime));

			Assert.assertEquals(result, "bar");
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
}
