package com.readytalk.staccato.database.migration.workflow;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;

/**
 * Context for a given workflow.
 */
public class WorkflowContextImpl implements WorkflowContext {
	private final MigrationAnnotationParser annotationParser;
	private final MigrationRuntime migrationRuntime;

	@Inject
	public WorkflowContextImpl(final MigrationAnnotationParser _annotationParser,
			@Assisted final MigrationRuntime _migrationRuntime) {
		this.annotationParser = _annotationParser;
		this.migrationRuntime = _migrationRuntime;
	}

	/* (non-Javadoc)
	 * @see com.readytalk.staccato.database.migration.workflow.WorkflowContext#getAnnotationParser()
	 */
	@Override
	public MigrationAnnotationParser getAnnotationParser() {
		return annotationParser;
	}

	/* (non-Javadoc)
	 * @see com.readytalk.staccato.database.migration.workflow.WorkflowContext#getMigrationRuntime()
	 */
	@Override
	public MigrationRuntime getMigrationRuntime() {
		return migrationRuntime;
	}
}
