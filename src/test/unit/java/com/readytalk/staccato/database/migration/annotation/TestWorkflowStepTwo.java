package com.readytalk.staccato.database.migration.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.readytalk.staccato.database.migration.workflow.WorkflowStepExecutorImpl;

/**
 * Test workflow step used in unit testing.
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
@WorkflowStep(executedBy = WorkflowStepExecutorImpl.class)
public @interface TestWorkflowStepTwo {

}
