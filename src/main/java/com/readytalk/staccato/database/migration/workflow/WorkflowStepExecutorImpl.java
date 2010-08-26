package com.readytalk.staccato.database.migration.workflow;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;

/**
 * @author jhumphrey
 */
public class WorkflowStepExecutorImpl<T extends Annotation> implements WorkflowStepExecutor<T> {

  Logger logger = Logger.getLogger(this.getClass().getName());

  private T workflowStep;

  @Override
  public void initialize(T workflowStep) {
    this.workflowStep = workflowStep;
  }

  @Override
  public Object execute(Object scriptInstance, WorkflowContext context) throws InvocationTargetException, IllegalAccessException {
    MigrationAnnotationParser annotationParser = context.getAnnotationParser();
    Method method = annotationParser.getAnnotatedMethod(scriptInstance, workflowStep.annotationType());

    Object result;

    try {
      logger.fine("Invoking workflow step @" + workflowStep.annotationType().getSimpleName() + " on method: " + method.getName());
      // first try invoking without runtime argument
      result = method.invoke(scriptInstance);
    } catch (IllegalArgumentException e) {
      // if exception is thrown then send the runtime
      result = method.invoke(scriptInstance, context.getMigrationRuntime());
    }

    return result;
  }
}
