package com.readytalk.staccato.database.migration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.readytalk.staccato.database.migration.workflow.WorkflowStepExecutorImpl;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for annotating methods that should be executed during the schema-up step of a migration
 *
 * @author jhumphrey
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
@WorkflowStep(executedBy = WorkflowStepExecutorImpl.class)
public @interface SchemaUp {
}
