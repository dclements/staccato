package com.readytalk.staccato.database.migration.workflow;

import static org.mockito.Mockito.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

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

  @Test(dataProvider = "jdbcProvider", enabled = true)
  public void testExecuteWithConnection(URI baseJdbcUri) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException {

    File file = new File("src/test/unit/groovy/TestScript.groovy");

    // test with a groovy script
    DynamicLanguageScript<?> script = mock(DynamicLanguageScript.class);
    when(script.getUrl()).thenReturn(file.toURI().toURL());
    when(script.getFilename()).thenReturn(file.getName());
    when(script.getUrl()).thenReturn(file.toURI().toURL());

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class<?> scriptClass = gcl.parseClass(new GroovyCodeSource(script.getUrl()));

    Object scriptInstance = scriptClass.newInstance();
    Method testMethod = scriptInstance.getClass().getMethod("create", MigrationRuntime.class);

    Create createWorkflowStep = testMethod.getAnnotation(Create.class);

    MigrationAnnotationParser annotationParser = mock(MigrationAnnotationParser.class);
    when(annotationParser.getAnnotatedMethod(eq(scriptInstance), eq(createWorkflowStep.annotationType()))).thenReturn(testMethod);

    CreateWorkflowStepExecutor executor = new CreateWorkflowStepExecutor();
    executor.initialize(createWorkflowStep);

    DatabaseContext context = new DatabaseContext();
    context.setDbName(dbName);
    context.setUsername(dbUser);
    context.setPassword(dbPwd);
    context.setRootDbName(rootDbName);
    context.setSuperUser(dbSuperUser);
    context.setSuperUserPwd(dbSuperUserPwd);
    context.setFullyQualifiedJdbcUri(URI.create(baseJdbcUri + dbName));
    context.setBaseJdbcUri(baseJdbcUri);
    context.setConnection(makeConnection(URI.create(baseJdbcUri.toString() + dbName)));
    context.setDatabaseType(DatabaseType.getTypeFromJDBCUri(baseJdbcUri));

    MigrationRuntime runtime = mock(MigrationRuntime.class);
    when(runtime.getDatabaseContext()).thenReturn(context, context);

    try {
      executor.execute(scriptInstance, new WorkflowContext(annotationParser, runtime));
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Test(dataProvider = "jdbcProvider", enabled = true)
  public void testExecuteWithOutConnection(URI baseJdbcUri) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException {

    File file = new File("src/test/unit/groovy/TestScript.groovy");

    // test with a groovy script
    DynamicLanguageScript<?> script = mock(DynamicLanguageScript.class);
    when(script.getUrl()).thenReturn(file.toURI().toURL());
    when(script.getFilename()).thenReturn(file.getName());
    when(script.getUrl()).thenReturn(file.toURI().toURL());

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class<?> scriptClass = gcl.parseClass(new GroovyCodeSource(script.getUrl()));

    Object scriptInstance = scriptClass.newInstance();
    Method testMethod = scriptInstance.getClass().getMethod("create", MigrationRuntime.class);

    Create createWorkflowStep = testMethod.getAnnotation(Create.class);

    MigrationAnnotationParser annotationParser = mock(MigrationAnnotationParser.class);
    when(annotationParser.getAnnotatedMethod(eq(scriptInstance), eq(createWorkflowStep.annotationType()))).thenReturn(testMethod);

    CreateWorkflowStepExecutor executor = new CreateWorkflowStepExecutor();
    executor.initialize(createWorkflowStep);

    DatabaseContext context = new DatabaseContext();
    context.setDbName(dbName);
    context.setUsername(dbUser);
    context.setPassword(dbPwd);
    context.setRootDbName(rootDbName);
    context.setSuperUser(dbSuperUser);
    context.setSuperUserPwd(dbSuperUserPwd);
    context.setFullyQualifiedJdbcUri(URI.create(baseJdbcUri + dbName));
    context.setBaseJdbcUri(baseJdbcUri);
    context.setDatabaseType(DatabaseType.getTypeFromJDBCUri(baseJdbcUri));

    MigrationRuntime runtime = mock(MigrationRuntime.class);
    when(runtime.getDatabaseContext()).thenReturn(context, context);

    try {
      executor.execute(scriptInstance, new WorkflowContext(annotationParser, runtime));
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
