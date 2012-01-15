package com.readytalk.staccato.database.migration.workflow;

import com.readytalk.staccato.database.migration.MigrationRuntime;

public interface WorkflowContextFactory {
	WorkflowContext create(MigrationRuntime migrationRuntime);
}
