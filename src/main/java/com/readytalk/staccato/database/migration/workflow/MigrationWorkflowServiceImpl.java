package com.readytalk.staccato.database.migration.workflow;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.MigrationLoggingService;
import com.readytalk.staccato.database.migration.MigrationResult;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.annotation.WorkflowStep;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

/**
 * Default implemenation for executing migrations.
 */
public class MigrationWorkflowServiceImpl<T extends Annotation> implements MigrationWorkflowService {

	private static final Logger logger = Logger.getLogger(MigrationWorkflowServiceImpl.class);

	private final MigrationAnnotationParser annotationParser;

	private final MigrationLoggingService migrationLoggingService;
	
	private final Injector injector;

	@Inject
	public MigrationWorkflowServiceImpl(final MigrationAnnotationParser annotationParser,
			final MigrationLoggingService migrationLoggingService, final Injector inj) {
		this.annotationParser = annotationParser;
		this.migrationLoggingService = migrationLoggingService;
		this.injector = inj;
	}

	@Override
	@SuppressWarnings("unchecked")
	public MigrationResult executeWorkflow(final DynamicLanguageScript<?> script,
			final Class<? extends Annotation>[] migrationWorkflow, final MigrationRuntime migrationRuntime) {

		String beginOutput = "Executing migration workflow: ";
		for (Class<? extends Annotation> aClass : migrationWorkflow) {
			beginOutput += aClass.getSimpleName() + " ";
		}

		logger.debug(beginOutput);

		MigrationResult migrationResult = new MigrationResult();

		for (Class<? extends Annotation> workflowStep : migrationWorkflow) {

			Annotation annotation = annotationParser.getMethodAnnotation(script.getScriptInstance(), workflowStep);

			if (annotation != null) {
				final Class<? extends WorkflowStepExecutor<T>> executor =
					(Class<? extends WorkflowStepExecutor<T>>) workflowStep.getAnnotation(WorkflowStep.class).executedBy();

				if (executor != null) {
					// instantiate and execute the step
					try {
						WorkflowStepExecutor<T> stepExecutorInstance = injector.getInstance(executor);
						stepExecutorInstance.initialize((T) annotation);
						WorkflowContextFactory wcf = injector.getInstance(WorkflowContextFactory.class);
						Object executionResult = stepExecutorInstance.execute(script.getScriptInstance(),
								wcf.create(migrationRuntime));
						migrationResult.getResultMap().put(annotation.annotationType(), executionResult);

						// if enabled, log successful script execution
						if (migrationRuntime.isLoggingEnabled()) {
							migrationLoggingService.log(migrationRuntime.getDatabaseContext(), script, workflowStep,
									annotationParser.getMigrationAnnotation(script.getScriptInstance()));
						}

					} catch (IllegalAccessException e) {
						throw new MigrationException("Unable to access workflow step class: "
								+ workflowStep.getSimpleName(), e);
					} catch (InvocationTargetException e) {
						throw new MigrationException("Error invoking workflow step: " + workflowStep.getSimpleName() 
								+ ", in script: " + script.getFilename(), e);
					}
				}
			} else {
				logger.debug("Skipping workflow step @" + workflowStep.getSimpleName() 
						+ ". No annotated methods found in script: " + script.getFilename());
			}
		}

		return migrationResult;
	}
}
