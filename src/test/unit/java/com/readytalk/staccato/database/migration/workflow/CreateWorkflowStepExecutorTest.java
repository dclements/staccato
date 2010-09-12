package com.readytalk.staccato.database.migration.workflow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.BaseTest;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.Create;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

/**
 * @author jhumphrey
 */
public class CreateWorkflowStepExecutorTest extends BaseTest {

  @Test(dataProvider = "jdbcProvider")
  public void testExecuteWithConnection(URI baseJdbcUri) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException {

    File file = new File("src/test/unit/groovy/TestScript.groovy");

    // test with a groovy script
    DynamicLanguageScript script = EasyMock.createStrictMock(DynamicLanguageScript.class);
    EasyMock.expect(script.getUrl()).andReturn(file.toURI().toURL());
    EasyMock.expect(script.getFilename()).andReturn(file.getName());
    EasyMock.expect(script.getUrl()).andReturn(file.toURI().toURL());
    EasyMock.replay(script);

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new GroovyCodeSource(script.getUrl()));

    Object scriptInstance = scriptClass.newInstance();
    Method testMethod = scriptInstance.getClass().getMethod("create", MigrationRuntime.class);

    Create createWorkflowStep = testMethod.getAnnotation(Create.class);

    MigrationAnnotationParser annotationParser = EasyMock.createStrictMock(MigrationAnnotationParser.class);
    EasyMock.expect(annotationParser.getAnnotatedMethod(scriptInstance, createWorkflowStep.annotationType())).andReturn(testMethod);
    EasyMock.replay(annotationParser);

    CreateWorkflowStepExecutor executor = new CreateWorkflowStepExecutor();
    executor.initialize(createWorkflowStep);

    DatabaseContext context = new DatabaseContext();
    context.setDbName(dbName);
    context.setUsername(dbUsername);
    context.setPassword(dbPassword);
    context.setRootDbName(rootDbName);
    context.setRootUsername(rootDbUsername);
    context.setRootPassword(rootDbPassword);
    context.setFullyQualifiedJdbcUri(URI.create(baseJdbcUri + dbName));
    context.setBaseJdbcUri(baseJdbcUri);
    context.setConnection(makeConnection(URI.create(baseJdbcUri.toString() + dbName)));
    context.setDatabaseType(DatabaseType.getTypeFromJDBCUri(baseJdbcUri));

    MigrationRuntime runtime = EasyMock.createMock(MigrationRuntime.class);
    EasyMock.expect(runtime.getDatabaseContext()).andReturn(context);
    EasyMock.replay(runtime);

    try {
      executor.execute(scriptInstance, new WorkflowContext(annotationParser, runtime));
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Test(dataProvider = "jdbcProvider")
  public void testExecuteWithOutConnection(URI baseJdbcUri) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException {

    File file = new File("src/test/unit/groovy/TestScript.groovy");

    // test with a groovy script
    DynamicLanguageScript script = EasyMock.createStrictMock(DynamicLanguageScript.class);
    EasyMock.expect(script.getUrl()).andReturn(file.toURI().toURL());
    EasyMock.expect(script.getFilename()).andReturn(file.getName());
    EasyMock.expect(script.getUrl()).andReturn(file.toURI().toURL());
    EasyMock.replay(script);

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new GroovyCodeSource(script.getUrl()));

    Object scriptInstance = scriptClass.newInstance();
    Method testMethod = scriptInstance.getClass().getMethod("create", MigrationRuntime.class);

    Create createWorkflowStep = testMethod.getAnnotation(Create.class);

    MigrationAnnotationParser annotationParser = EasyMock.createStrictMock(MigrationAnnotationParser.class);
    EasyMock.expect(annotationParser.getAnnotatedMethod(scriptInstance, createWorkflowStep.annotationType())).andReturn(testMethod);
    EasyMock.replay(annotationParser);

    CreateWorkflowStepExecutor executor = new CreateWorkflowStepExecutor();
    executor.initialize(createWorkflowStep);

    DatabaseContext context = new DatabaseContext();
    context.setDbName(dbName);
    context.setUsername(dbUsername);
    context.setPassword(dbPassword);
    context.setRootDbName(rootDbName);
    context.setRootUsername(rootDbUsername);
    context.setRootPassword(rootDbPassword);
    context.setFullyQualifiedJdbcUri(URI.create(baseJdbcUri + dbName));
    context.setBaseJdbcUri(baseJdbcUri);
    context.setDatabaseType(DatabaseType.getTypeFromJDBCUri(baseJdbcUri));

    MigrationRuntime runtime = EasyMock.createMock(MigrationRuntime.class);
    EasyMock.expect(runtime.getDatabaseContext()).andReturn(context);
    EasyMock.replay(runtime);

    try {
      executor.execute(scriptInstance, new WorkflowContext(annotationParser, runtime));
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
