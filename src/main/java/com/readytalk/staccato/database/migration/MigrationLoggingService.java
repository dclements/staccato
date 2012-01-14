package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;

import com.google.inject.ImplementedBy;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.script.DynamicLanguageScript;

@ImplementedBy(MigrationLoggingServiceImpl.class)
public interface MigrationLoggingService {

	static final String MIGRATION_VERSIONS_TABLE = "STACCATO_MIGRATIONS";

	/**
	 * Creates the migration versions table
	 *
	 * @param context the database context
	 */
	void createVersionsTable(DatabaseContext context);

	/**
	 * Returns true if the migration versions table exists
	 *
	 * @param context the database context
	 * @return true if exists, false otherwise
	 */
	boolean versionTableExists(DatabaseContext context);

	/**
	 * Logs the script and associated workflow to the migrations versions table
	 *
	 * @param datbaseContext the datbase context
	 * @param script the script
	 * @param workflowStep the workflow step
	 * @param migrationAnnotation the migration annotation
	 */
	void log(DatabaseContext datbaseContext, DynamicLanguageScript<?> script, Class<? extends Annotation> workflowStep, Migration migrationAnnotation);
}
