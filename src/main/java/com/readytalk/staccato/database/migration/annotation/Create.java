package com.readytalk.staccato.database.migration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.readytalk.staccato.database.migration.workflow.CreateWorkflowStepExecutor;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for annotating methods that should be executed when performing the
 * DataUp step of a migration.
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
@WorkflowStep(executedBy = CreateWorkflowStepExecutor.class)
public @interface Create {
	
}
