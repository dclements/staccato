package com.ecovate.database.migration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ecovate.database.migration.workflow.WorkflowStepExecutorImpl;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Test workflow step used in unit testing
 *
 * @author jhumphrey
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
@WorkflowStep(executedBy = WorkflowStepExecutorImpl.class)
public @interface TestWorkflowStepOne {
  String value() default "";
}
