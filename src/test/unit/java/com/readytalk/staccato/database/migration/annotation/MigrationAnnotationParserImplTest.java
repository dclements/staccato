package com.readytalk.staccato.database.migration.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;
import groovy.lang.GroovyClassLoader;

/**
 * @author jhumphrey
 */
public class MigrationAnnotationParserImplTest {

  @Test
  public void testIsMigrationScript() throws IOException {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    @SuppressWarnings("unchecked")
    Class<? extends DynamicLanguageScript> scriptClass = gcl.parseClass(new File("src/test/unit/groovy/TestScript.groovy"));

    MigrationAnnotationParserImpl parser = new MigrationAnnotationParserImpl();
    Assert.assertTrue(parser.isMigrationScript(scriptClass));
  }

  @Test
  public void testIsNotMigrationScript() throws IOException {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    @SuppressWarnings("unchecked")
    Class<? extends DynamicLanguageScript> scriptClass = gcl.parseClass(new File("src/test/unit/groovy/BadTestScript.groovy"));

    MigrationAnnotationParserImpl parser = new MigrationAnnotationParserImpl();
    Assert.assertFalse(parser.isMigrationScript(scriptClass));
  }

  @Test
  public void testGetAnnotationSuccess() throws IOException, IllegalAccessException, InstantiationException {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new File("src/test/unit/groovy/TestScript.groovy"));

    Object scriptInstance = scriptClass.newInstance();

    MigrationAnnotationParserImpl parser = new MigrationAnnotationParserImpl();

    try {
      Annotation annotation = parser.getMethodAnnotation(scriptInstance, DataUp.class);

      Assert.assertNotNull(annotation);
      Assert.assertEquals(annotation.annotationType(), DataUp.class);

    } catch (MigrationException e) {
      Assert.fail("should not fail because the TestGroovy.groovy has no duplicate migration annotations");
    }
  }

  @Test
  public void testGetAnnotationFailure() throws IOException, IllegalAccessException, InstantiationException {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new File("src/test/unit/groovy/BadTestScript.groovy"));

    Object scriptInstance = scriptClass.newInstance();

    MigrationAnnotationParserImpl parser = new MigrationAnnotationParserImpl();

    try {
      Annotation annotation = parser.getMethodAnnotation(scriptInstance, PreUp.class);

      Assert.assertNull(annotation);

    } catch (MigrationException e) {
      Assert.fail("should not throw an exception");
    }
  }

  @Test
  public void testGetAnnotatedMethodSuccess() throws IOException, IllegalAccessException, InstantiationException {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new File("src/test/unit/groovy/TestScript.groovy"));

    Object scriptInstance = scriptClass.newInstance();

    MigrationAnnotationParserImpl parser = new MigrationAnnotationParserImpl();

    try {
      Method method = parser.getAnnotatedMethod(scriptInstance, DataUp.class);

      Assert.assertNotNull(method);
      Assert.assertEquals(method.getName(), "dataUp");

    } catch (MigrationException e) {
      Assert.fail("should not throw an exception");
    }
  }

  @Test
  public void testGetAnnotatedMethodNonExist() throws IOException, IllegalAccessException, InstantiationException {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new File("src/test/unit/groovy/BadTestScript.groovy"));

    Object scriptInstance = scriptClass.newInstance();

    MigrationAnnotationParserImpl parser = new MigrationAnnotationParserImpl();

    try {
      Method method = parser.getAnnotatedMethod(scriptInstance, PreUp.class);

      Assert.assertNull(method);

    } catch (MigrationException e) {
      Assert.fail("should not throw an exception");
    }
  }

  @Test
  public void testGetAnnotatedMethodNonUnique() throws IOException, IllegalAccessException, InstantiationException {

    GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
    Class scriptClass = gcl.parseClass(new File("src/test/unit/groovy/BadTestScript.groovy"));

    Object scriptInstance = scriptClass.newInstance();

    MigrationAnnotationParserImpl parser = new MigrationAnnotationParserImpl();

    try {
      parser.getAnnotatedMethod(scriptInstance, DataUp.class);
      Assert.fail("should have thrown exception because " + DataUp.class.getName() + " is not unique");
    } catch (MigrationException e) {
      // no-op, success
    }
  }
}
