package com.readytalk.staccato.database.migration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.readytalk.staccato.database.migration.workflow.WorkflowStepExecutor;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Link between a workflow step annotation and its execution implementations.
 * <p/>
 * A given workflow step annotation should be annotated by a <code>@WorkflowStep</code>
 * annotation which refers to its list of execution implementations.
 *
 * @author jhumphrey
 */
@Documented
@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface WorkflowStep {
  /**
   * <code>WorkflowStepExecutor</code> classes must reference distinct target types.
   *
   * If two <code>WorkflowStepExecutor</code> refer to the same type,
   * a {@link com.readytalk.staccato.database.migration.MigrationException} will occur.
   *
   * @return array of WorkflowStepExecutor classes implementing the workflow step
   */
  public Class<? extends WorkflowStepExecutor> executedBy();
}
