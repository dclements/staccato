package com.readytalk.staccato.database.migration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.google.inject.ImplementedBy;

/**
 * Parses migration annotations from class files
 *
 * @author jhumphrey
 */
@ImplementedBy(MigrationAnnotationParserImpl.class)
public interface MigrationAnnotationParser {

  /**
   * Returns the {@link com.readytalk.staccato.database.migration.annotation.Migration} annotation for a given script instance
   *
   * @param scriptInstance the script instance
   * @return {@link com.readytalk.staccato.database.migration.annotation.Migration}
   */
  public Migration getMigrationAnnotation(Object scriptInstance);

  /**
   * Determines whether a given class is a migration script.  Migration scripts must
   * be annotated with the {@link Migration} annotation
   *
   * @param scriptClass the script class
   * @return true if the script if a migration script, false otherwise
   */
  public boolean isMigrationScript(Class<?> scriptClass);

  /**
   * Parses the given class for a method annotation with the workflow step specified
   *
   * @param scriptInstance the script instance
   * @param annotation the workflow step annotation
   * @return the annotation
   */
  public <T extends Annotation> T getMethodAnnotation(Object scriptInstance, Class<? extends Annotation> annotation);

  /**
   * Returns the object instance method annotated with the annotated specified
   *
   * @param scriptInstance the script instance
   * @param annotation the annotated
   * @return the method annotated with the specified annotation
   */
  public Method getAnnotatedMethod(Object scriptInstance, Class<? extends Annotation> annotation);
}
