package com.ecovate.database.migration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ecovate.database.migration.workflow.WorkflowStepExecutorImpl;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for annotating methods that should be executed during the pre-up step of a migration
 *
 * @author jhumphrey
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
@WorkflowStep(executedBy = WorkflowStepExecutorImpl.class)
public @interface PreUp {

}
