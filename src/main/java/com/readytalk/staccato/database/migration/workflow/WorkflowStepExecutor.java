package com.readytalk.staccato.database.migration.workflow;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

/**
 * Interface for workflow step execution.
 */
public interface WorkflowStepExecutor<T extends Annotation> {
	/**
	 * Initialize the workflow executor in preparation for execution calls.
	 * The workflow step annotation for a given execution declaration
	 * is passed.
	 *
	 * @param workflowStep annotation instance for a given workflow step
	 */
	void initialize(T workflowStep);

	/**
	 * Execute a Method for a particular workflow step.
	 *
	 * @param scriptInstance the instance of the script being executed
	 * @param context context in which the workflow is executed
	 * @throws IllegalAccessException if there are errors during migration method invocation
	 * @throws java.lang.reflect.InvocationTargetException if there are errors during migration method invocation
	 * @return the return object
	 */
	Object execute(Object scriptInstance, WorkflowContext context) throws InvocationTargetException, IllegalAccessException;
}
