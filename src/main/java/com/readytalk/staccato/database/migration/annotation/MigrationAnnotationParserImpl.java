package com.readytalk.staccato.database.migration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.readytalk.staccato.database.migration.MigrationException;

public class MigrationAnnotationParserImpl implements MigrationAnnotationParser {

	public static final Logger logger = Logger.getLogger(MigrationAnnotationParserImpl.class);

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

		logger.trace("Getting annotation instance for workflow step: " + workflowStep.getSimpleName());

		T annotation = null;

		Method annotatedMethod = getAnnotatedMethod(scriptInstance, workflowStep);

		if (annotatedMethod != null) {
			annotation = (T) annotatedMethod.getAnnotation(workflowStep);
		}

		return annotation;
	}

	@Override
	public Method getAnnotatedMethod(Object scriptInstance, Class<? extends Annotation> annotation) {

		logger.trace("Looking for method annotated with: " + annotation.getSimpleName());

		Method[] methods = scriptInstance.getClass().getMethods();

		Method annotatedMethod = null;

		for (Method method : methods) {
			if (method.isAnnotationPresent(annotation)) {
				if (annotatedMethod != null) {
					throw new MigrationException("only one method may be annotated with [" + annotation.getName() + "]");
				} else {
					annotatedMethod = method;
					logger.trace("found method: " + annotatedMethod.getName());
				}
			}
		}

		return annotatedMethod;
	}

	@Override
	public boolean containsWorkflowSteps(Object scriptInstance, Class<? extends Annotation>[] workflowSteps) {

		for (Class<? extends Annotation> workflowStep : workflowSteps) {
			Method[] methods = scriptInstance.getClass().getMethods();

			for (Method method : methods) {
				if (method.isAnnotationPresent(workflowStep)) {
					return true;
				}
			}
		}

		return false;
	}
}
