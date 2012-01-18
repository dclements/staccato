package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.workflow.MigrationWorkflowService;

public class GroovyMigrationService implements MigrationService<GroovyScript> {

	private static final Logger logger = Logger.getLogger(GroovyMigrationService.class);

	private final MigrationWorkflowService migrationWorkflowService;
	private final MigrationAnnotationParser annotationParser;

	@Inject
	public GroovyMigrationService(
			MigrationWorkflowService migrationWorkflowService,
			MigrationAnnotationParser annotationParser) {

		this.migrationWorkflowService = migrationWorkflowService;
		this.annotationParser = annotationParser;
	}

	@Override
	public void run(List<GroovyScript> migrationScripts, MigrationRuntime migrationRuntime) {

		if (migrationScripts.size() > 0) {
			DatabaseContext databaseContext = migrationRuntime.getDatabaseContext();

			MigrationType migrationType = migrationRuntime.getMigrationType();

			String workflowOutput = "workflow: ";
			for (Class<? extends Annotation> aClass : migrationType.getWorkflowSteps()) {
				workflowOutput += aClass.getSimpleName() + " ";
			}

			logger.info("Running " + databaseContext.getDatabaseType() + " migration: " + migrationType.name() + ", " + workflowOutput + ", for database: " +
					databaseContext.getFullyQualifiedJdbcUri());

			// iterate through the groovy scripts for invocation
			for (GroovyScript script : migrationScripts) {

				Migration migrationAnnotation = annotationParser.getMigrationAnnotation(script.getScriptInstance());

				// validate database type


				if (!isValidDatabaseType(migrationAnnotation, databaseContext)) {
					logger.debug("Excluding " + String.valueOf(migrationAnnotation.databaseType()) + " script from execution: " + String.valueOf(script.getFilename()));
					continue;
				}

				//TODO: Figure out transactions
				// i'd like to create a transaction prior to each individual script execution so that if
				// there are errors, I can rollback anything that was done.
				// I spent a ton of time trying to get transactions to work without luck.
				//databaseService.startTransaction(databaseContext, script);

				if (annotationParser.containsWorkflowSteps(script.getScriptInstance(), migrationType.getWorkflowSteps())) {
					logger.info("Executing script: " + script.getFilename());

					// print the description to the logs if it's not null
					String description = getDescription(migrationAnnotation);
					if (getDescription(migrationAnnotation) != null) {
						logger.info("Description: " + description);
					}

					migrationWorkflowService.executeWorkflow(script, migrationType.getWorkflowSteps(), migrationRuntime);
				}

				//TODO: add this back in once transactions are figured out
				//databaseService.endTransaction(databaseContext, script);
			}
		}
	}

	/**
	 * Helper method to get the description from the migration annotation
	 *
	 * @param migrationAnnotation the migration annotation
	 * @return the description
	 */
	String getDescription(Migration migrationAnnotation) {
		try {
			return migrationAnnotation.description();
		} catch (IncompleteAnnotationException e) {
			return null;
		}
	}

	/**
	 * Helper method to validate that the script we're executing belongs to this database migration.
	 * If the Migration annotation database type is not equal to the
	 * {@link com.readytalk.staccato.database.DatabaseContext} database type then do not execute
	 * this script
	 *
	 * @param script the script
	 * @param scriptDatabaseType The database type from a migrationAnnotation.
	 * @param databaseContext the database context
	 * @return true if valid, false otherwise
	 */
	boolean isValidDatabaseType(Migration migration, DatabaseContext databaseContext) {
		DatabaseType scriptDatabaseType;
		try {
			scriptDatabaseType = migration.databaseType();
		} catch(IncompleteAnnotationException iae) {
			return true;
		}

		if (scriptDatabaseType != null && scriptDatabaseType != databaseContext.getDatabaseType()) {
			return false;
		}
		return true;
	}
}
