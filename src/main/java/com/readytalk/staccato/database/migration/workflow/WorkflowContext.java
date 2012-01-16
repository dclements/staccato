package com.readytalk.staccato.database.migration.workflow;

import com.readytalk.staccato.database.migration.MigrationRuntime;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;

public interface WorkflowContext {

	MigrationAnnotationParser getAnnotationParser();

	MigrationRuntime getMigrationRuntime();

}