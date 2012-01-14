package com.readytalk.staccato.database.migration.workflow;

import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;

/**
 * Context for a given workflow.
 */
public class WorkflowContext {
	private final MigrationAnnotationParser annotationParser;
	private final MigrationRuntime migrationRuntime;

	public WorkflowContext(MigrationAnnotationParser annotationParser, MigrationRuntime migrationRuntime) {
		this.annotationParser = annotationParser;
		this.migrationRuntime = migrationRuntime;
	}

	public MigrationAnnotationParser getAnnotationParser() {
		return annotationParser;
	}

	public MigrationRuntime getMigrationRuntime() {
		return migrationRuntime;
	}
}
