package com.readytalk.staccato.database.migration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.readytalk.staccato.database.migration.MigrationException;

/**
 * @author jhumphrey
 */
public class MigrationAnnotationParserImpl implements MigrationAnnotationParser {

  @Override
  public Migration getMigrationAnnotation(Object scriptInstance) {
    return scriptInstance.getClass().getAnnotation(Migration.class);
  }

  @Override
  public boolean isMigrationScript(Class<?> scriptClass) {
    return scriptClass.isAnnotationPresent(Migration.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Annotation> T getMethodAnnotation(Object scriptInstance, Class<? extends Annotation> workflowStep) {

    T annotation = null;

    Method annotatedMethod = getAnnotatedMethod(scriptInstance, workflowStep);

    if (annotatedMethod != null) {
      annotation = (T) annotatedMethod.getAnnotation(workflowStep);
    }
    return annotation;
  }

  @Override
  public Method getAnnotatedMethod(Object scriptInstance, Class<? extends Annotation> annotation) {

    Method[] methods = scriptInstance.getClass().getMethods();

    Method annotatedMethod = null;

    for (Method method : methods) {
      if (method.isAnnotationPresent(annotation)) {
        if (annotatedMethod != null) {
          throw new MigrationException("only one method may be annotated with [" + annotation.getName() + "]");
        } else {
          annotatedMethod = method;
        }
      }
    }

    return annotatedMethod;
  }
}
