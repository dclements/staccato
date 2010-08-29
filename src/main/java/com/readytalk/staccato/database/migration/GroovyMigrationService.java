package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.DatabaseService;
import com.readytalk.staccato.database.DatabaseType;
import com.readytalk.staccato.database.migration.annotation.Migration;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;
import com.readytalk.staccato.database.migration.script.groovy.GroovyScript;
import com.readytalk.staccato.database.migration.workflow.MigrationWorkflowService;

/**
 * @author jhumphrey
 */
public class GroovyMigrationService implements MigrationService<GroovyScript> {

  public static final Logger logger = Logger.getLogger(GroovyMigrationService.class);

  private MigrationWorkflowService migrationWorkflowService;
  private MigrationAnnotationParser annotationParser;
  private MigrationVersionsService migrationVersionsService;

  @Inject
  public GroovyMigrationService(
    MigrationWorkflowService migrationWorkflowService,
    MigrationAnnotationParser annotationParser,
    MigrationVersionsService migrationVersionsService) {

    this.migrationWorkflowService = migrationWorkflowService;
    this.annotationParser = annotationParser;
    this.migrationVersionsService = migrationVersionsService;
  }

  @Override
  public void run(List<GroovyScript> migrationScripts, MigrationRuntime migrationRuntime) {

    DatabaseContext databaseContext = migrationRuntime.getDatabaseContext();
    MigrationType migrationType = migrationRuntime.getMigrationType();

    String workflowOutput = "workflow: ";
    for (Class<? extends Annotation> aClass : migrationType.getWorkflowSteps()) {
      workflowOutput += aClass.getSimpleName() + " ";
    }

    logger.info("Running " + databaseContext.getDatabaseType() + " migration: " + migrationType.name() + ", " + workflowOutput);

    // iterate through the groovy scripts for invocation
    for (GroovyScript script : migrationScripts) {
      logger.info("Executing script: " + script.getFilename());

      Migration migrationAnnotation = annotationParser.getMigrationAnnotation(script.getScriptInstance());

      // print the description to the logs if it's not null
      String description = getDescription(migrationAnnotation);
      if (getDescription(migrationAnnotation) != null) {
        logger.info("Description: " + description);
      }

      // validate database type
      if (!isValidDatabaseType(script, migrationAnnotation, databaseContext)) {
        continue;
      }

      // todo: Figure out transactions
      // i'd like to create a transaction prior to each individual script execution so that if
      // there are errors, I can rollback anything that was done.
      // I spent a ton of time trying to get transactions to work without luck.
      // databaseService.startTransaction(databaseContext, script);

      migrationWorkflowService.executeWorkflow(script, migrationType.getWorkflowSteps(), migrationRuntime);

      // if execute is successful, log to the migration versions table
      migrationVersionsService.log(databaseContext, script);

      // todo: add this back in once transactions are figured out
//          databaseService.endTransaction(databaseContext, script);
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
   * @param migrationAnnotation the migration annotation
   * @param databaseContext the database context
   * @return true if valid, false otherwise
   */
  boolean isValidDatabaseType(GroovyScript script, Migration migrationAnnotation, DatabaseContext databaseContext) {
    try {
      DatabaseType scriptDatabaseType = migrationAnnotation.databaseType();
      if (scriptDatabaseType != null && scriptDatabaseType != databaseContext.getDatabaseType()) {
        logger.debug("Excluding " + scriptDatabaseType + " script from execution: " + script.getFilename());
        return false;
      }
      return true;
    } catch (Exception e) {
      return true;
    }
  }
}
